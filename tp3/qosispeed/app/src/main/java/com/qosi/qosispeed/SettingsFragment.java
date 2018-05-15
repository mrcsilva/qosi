package com.qosi.qosispeed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class SettingsFragment extends Fragment {

    View myView;
    private Switch bandSwitch;
    private Switch pingSwitch;
    private Switch jitterSwitch;


    private void updateValues() {
        File file = new File(getContext().getFilesDir(), "settings.json");
        JSONObject st = new JSONObject();
        try {
            st.accumulate(getString(R.string.bandwidth), bandSwitch.isChecked());
            st.accumulate(getString(R.string.delay), pingSwitch.isChecked());
            st.accumulate(getString(R.string.jitter), jitterSwitch.isChecked());
            FileOutputStream out = getContext().openFileOutput("settings.json", Context.MODE_PRIVATE);
            out.write(st.toString().getBytes());
        }
        catch(IOException | JSONException je) {
            je.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_layout, container, false);
        bandSwitch = (Switch) myView.findViewById(R.id.bandwidth_switch);
        pingSwitch = (Switch) myView.findViewById(R.id.ping_switch);
        jitterSwitch = (Switch) myView.findViewById(R.id.jitter_switch);


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
                bandSwitch.setChecked((Boolean) st.get(getString(R.string.bandwidth)));
                pingSwitch.setChecked((Boolean) st.get(getString(R.string.delay)));
                jitterSwitch.setChecked((Boolean) st.get(getString(R.string.jitter)));
            }
            else {
                Log.i("mario", "Yup, null");
            }
        }
        catch(IOException | JSONException io) {
            io.printStackTrace();
        }

        bandSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == myView.findViewById(R.id.bandwidth_switch)) {
                    updateValues();
                }
            }
        });

        pingSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == myView.findViewById(R.id.ping_switch)) {
                    updateValues();
                }
            }
        });

        jitterSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == myView.findViewById(R.id.jitter_switch)) {
                    updateValues();
                }
            }
        });
        return myView;
    }
}
