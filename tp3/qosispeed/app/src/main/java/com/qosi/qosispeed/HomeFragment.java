package com.qosi.qosispeed;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    View myView;
    private SettingsFragment sf = null;
    private TextView tv;
    private Button start;
    private boolean one;
    private String text = "";
    private FragmentManager fm = null;
    private ProgressBar p;
    private IperfTask iperfTask = null;
    private boolean firstBoot = true;


    public void setSettings(SettingsFragment sf) {
        this.sf = sf;
    }

    public SettingsFragment getSettingsFragment() {
        return this.sf;
    }


    public void doOptions() {
        View settingsView = sf.getView();
        if(settingsView != null) {
            Switch band = (Switch) settingsView.findViewById(R.id.bandwidth_switch);
            Switch ping = (Switch) settingsView.findViewById(R.id.ping_switch);
            Switch jitter = (Switch) settingsView.findViewById(R.id.jitter_switch);

            String options = "";
            if (band.isChecked()) {
                options += "- Bandwidth\n";
                one = true;
            }
            else {
                one = false;
            }
            if (ping.isChecked()) {
                options += "- Delay (Ping)\n";
                one = true;
            }
            else if(!one){
                one = false;
            }
            if (jitter.isChecked()) {
                options += "- Jitter\n";
                one = true;
            }
            else if (!one){
                one = false;
            }

            if (!one) {
                text = "Go to Settings and choose what to test!";
            } else {
                text = "Tests to perform:\n" + options;
            }
        }
        else if(!one){
            tv.setText("Go to Settings and choose what to test!");
            start.setEnabled(false);
            start.setBackgroundColor(Color.parseColor("grey"));
        }
        else {
            tv.setText(text);
            start.setEnabled(true);
            start.setBackgroundColor(Color.parseColor("#008cff"));
        }
    }

    public void initIperf(ResultsFragment results) {
        InputStream in;
        try {
            //The asset "iperf" (from assets folder) inside the activity is opened for reading.
            in = getResources().getAssets().open("iperf3");
        } catch (IOException e2) {
            tv.append("\nError occurred while accessing system resources, please reboot and try again.");
            return;
        }
        try {
            //Checks if the file already exists, if not copies it.
            new FileInputStream(getContext().getFilesDir().getAbsolutePath() + "/iperf3");
        } catch (FileNotFoundException e1) {
            try {
                //The file named "iperf" is created in a system designated folder for this application.
                OutputStream out = new FileOutputStream(getContext().getFilesDir().getAbsolutePath() + "/iperf3", false);
                // Transfer bytes from "in" to "out"
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                //After the copy operation is finished, we give execute permissions to the "iperf" executable using shell commands.
                Process processChmod = Runtime.getRuntime().exec("/system/bin/chmod 777 " + getContext().getFilesDir().getAbsolutePath() + "/iperf3");
                // Executes the command and waits untill it finishes.
                processChmod.waitFor();
            } catch (IOException e) {
                tv.append("\nError occurred while accessing system resources, please reboot and try again.");
                return;
            } catch (InterruptedException e) {
                tv.append("\nError occurred while accessing system resources, please reboot and try again.");
                return;
            }
            //Creates an instance of the class IperfTask for running an iperf test, then executes.
            Log.i("iperf", "Executa iperf!");
            iperfTask.start();
            return;
        }
        //Creates an instance of the class IperfTask for running an iperf test, then executes.
        Log.i("iperf", "Executa iperf!");
        iperfTask.start();
        return;
    }


    private void startTest() {
        Boolean band = false;
        Boolean ping = false;
        Boolean jitter = false;
        JSONObject st = null;
        try {
            File f = new File(getContext().getFilesDir(), "settings.json");
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            if(sb.toString().length() > 10) {
                st = new JSONObject(sb.toString());
            }
            if(st != null) {
                Log.i("mario", st.toString());
                band = (Boolean) st.get(getString(R.string.bandwidth));
                ping = (Boolean) st.get(getString(R.string.delay));
                jitter = (Boolean) st.get(getString(R.string.jitter));
            }
            else {
                Log.i("mario", "Yup, null");
            }
        }
        catch(IOException | JSONException io) {
            io.printStackTrace();
        }

        if(st != null) {
            final Boolean band2 = band;
            final Boolean ping2 = ping;
            final Boolean jitter2 = jitter;

            p.setVisibility(View.VISIBLE);

            iperfTask = new IperfTask(getContext().getFilesDir().getAbsolutePath());

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    PingThread pt = new PingThread();
                    ResultsFragment results = new ResultsFragment();
                    if (band2) {
                        initIperf(results);

                        synchronized (iperfTask) {
                            try {
                                iperfTask.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (ping2 || jitter2) {

                        pt.start();

                        synchronized (pt) {
                            try {
                                pt.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    Log.i("mario", "Saiu!");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            p.setVisibility(View.INVISIBLE);
                        }
                    });

                    results.setResults(iperfTask.getResult(), ping2, jitter2, pt.getResult());
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, results).commit();
                }
            });

            t.start();

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);
        tv = (TextView) myView.findViewById(R.id.enabledOptions);
        start = (Button) myView.findViewById(R.id.startButton);
        p = (ProgressBar) myView.findViewById(R.id.wait_results);
        p.setVisibility(View.INVISIBLE);

        p.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor("#3f51b5")));


        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                p.setVisibility(View.VISIBLE);
                if (v == myView.findViewById(R.id.startButton)) {
                    startTest();
                }
            }
        });

        if(firstBoot) {
            JSONObject st = null;
            String options = "";
            boolean one = false;
            try {
                File f = new File(getContext().getFilesDir(), "settings.json");
                if(f.length() != 0) {
                    BufferedReader in = new BufferedReader(new FileReader(f));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.toString().length() > 10) {
                        st = new JSONObject(sb.toString());
                    }
                    if (st != null) {
                        Log.i("mario", st.toString());
                        if ((Boolean) st.get("Bandwidth")) {
                            options += "- Bandwidth\n";
                            one = true;
                        } else {
                            one = false;
                        }
                        if ((Boolean) st.get("Delay")) {
                            options += "- Delay (Ping)\n";
                            one = true;
                        } else if (!one) {
                            one = false;
                        }
                        if ((Boolean) st.get("Jitter")) {
                            options += "- Jitter\n";
                            one = true;
                        } else if (!one) {
                            one = false;
                        }

                        if (!one) {
                            tv.setText("Go to Settings and choose what to test!");
                        } else {
                            tv.setText("Tests to perform:\n" + options);
                        }
                    } else {
                        tv.setText("Go to Settings and choose what to test!");
                    }
                }
                else {
                    options += "- Bandwidth\n";
                    options += "- Delay (Ping)\n";
                    options += "- Jitter\n";
                    tv.setText("Tests to perform:\n" + options);

                    st = new JSONObject();
                    st.accumulate(getString(R.string.bandwidth), true);
                    st.accumulate(getString(R.string.delay), true);
                    st.accumulate(getString(R.string.jitter), true);
                    FileOutputStream out = getContext().openFileOutput("settings.json", Context.MODE_PRIVATE);
                    out.write(st.toString().getBytes());
                }
            }
            catch(IOException | JSONException io) {
                io.printStackTrace();
            }
        }
        else {
            doOptions();
        }

        return myView;
    }
}



class PingThread extends Thread {

    private Process process = null;

    private StringBuilder sb = new StringBuilder();

    private String res = "";


    public String getResult() {
        return this.res;
    }

    @Override
    public void run() {
        String str = "ping -c 5 ping.online.net";
        synchronized (this) {
            try {
                //The user input for the parameters is parsed into a string list as required from the ProcessBuilder Class.
                process = Runtime.getRuntime().exec(str);
                process.waitFor();

                Log.i("ping", "Le resultado!");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    //This is used to pass the output to the thread running the GUI, since this is separate thread.
                    sb.append(line);
                    Log.i("ping out", line);
                }
                Log.i("ping", "Fechou output!");
                reader.close();
                process.destroy();
                res = sb.toString();
                Pattern re = Pattern.compile("(\\s([0-9.]+(/?))+)\\sms$",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                Matcher m = re.matcher(res);
                Log.i("ping", sb.toString());
                if(m.find()) {
                    res = m.group(1) + " ms";
                }
                notify();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class IperfTask extends Thread {

    private Process process = null;

    private StringBuilder download = new StringBuilder();
    private StringBuilder upload = new StringBuilder();

    private String res = "";

    private String path;

    public IperfTask(String path) {
        this.path = path;
    }


    public String getResult() {
        return res;
    }

    private void parseResult() {
        try {
            JSONObject down = new JSONObject(download.toString());
            JSONObject up = new JSONObject(upload.toString());

            Double dbps = (Double) down.getJSONObject("end").getJSONObject("sum_received").get("bits_per_second");
            Double ubps = (Double) up.getJSONObject("end").getJSONObject("sum_sent").get("bits_per_second");
            int dcount = 0;
            int ucount = 0;

            while(dbps > 1000) {
                dbps /= 1024;
                dcount ++;
            }
            while(ubps > 1000) {
                ubps /= 1024;
                ucount ++;
            }
            switch(dcount) {
                case 0:
                    res += new DecimalFormat("##.##").format(dbps) + " bps;";
                    break;
                case 1:
                    res += new DecimalFormat("##.##").format(dbps) + " Kbps;";
                    break;
                case 2:
                    res += new DecimalFormat("##.##").format(dbps) + " Mbps;";
                    break;
                case 3:
                    res += new DecimalFormat("##.##").format(dbps) + " Gbps;";
                    break;
            }
            switch(ucount) {
                case 0:
                    res += new DecimalFormat("##.##").format(ubps) + " bps";
                    break;
                case 1:
                    res += new DecimalFormat("##.##").format(ubps) + " Kbps";
                    break;
                case 2:
                    res += new DecimalFormat("##.##").format(ubps) + " Mbps";
                    break;
                case 3:
                    res += new DecimalFormat("##.##").format(ubps) + " Gbps";
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //This function is used to implement the main task that runs on the background.
    @Override
    public void run() {
        // iperf command syntax check using a Regular expression to protect the system from user exploitation.
        String str = path + "/iperf3 -c ping.online.net -p 5206 -J";
        String str2 = path + "/iperf3 -R -c ping.online.net -p 5206 -J";
        synchronized (this) {
            try {
                //The user input for the parameters is parsed into a string list as required from the ProcessBuilder Class.
                process = Runtime.getRuntime().exec(str2);
                process.waitFor();
                //A buffered output of the stdout is being initialized so the iperf output could be displayed on the screen.
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                Log.i("iperf", "Le resultado download!");
                while ((line = reader.readLine()) != null) {
                    //This is used to pass the output to the thread running the GUI, since this is separate thread.
                    if(line.contains("error")) {
                        download.append(line);
                        Log.i("iperf out", line);
                        break;
                    }
                    else {
                        download.append(line);
                        Log.i("iperf out", line);
                    }
                }

                process = Runtime.getRuntime().exec(str);
                process.waitFor();
                //A buffered output of the stdout is being initialized so the iperf output could be displayed on the screen.
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                Log.i("iperf", "Le resultado upload!");
                while ((line = reader.readLine()) != null) {
                    //This is used to pass the output to the thread running the GUI, since this is separate thread.
                    if(line.contains("error")) {
                        upload.append(line);
                        Log.i("iperf out", line);
                        break;
                    }
                    else {
                        upload.append(line);
                        Log.i("iperf out", line);
                    }
                }

                Log.i("iperf", "Fechou output!");
                reader.close();
                process.destroy();
                parseResult();
                notify();
            }
            catch (IOException | InterruptedException e) {
                res = "\nError occurred while accessing system resources, please reboot and try again.";
                e.printStackTrace();
            }
        }
    }
}


