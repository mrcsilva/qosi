package com.qosi.qosispeed;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    View myView;
    private SettingsFragment sf = null;
    private TextView tv;
    private Button start;
    private boolean one;
    private String text = "";


    public void setSettings(SettingsFragment sf) {
        this.sf = sf;
    }

    public void doOptions() {
        View settingsView = sf.getView();
        if(settingsView != null) {
            Switch band = (Switch) settingsView.findViewById(R.id.bandwidth_switch);
            Switch ping = (Switch) settingsView.findViewById(R.id.ping_switch);
            Switch jitter = (Switch) settingsView.findViewById(R.id.jitter_switch);
            String options = "";
            if (band.isChecked()) {
                System.out.println("Band!");
                options += "- Bandwidth\n";
                one = true;
            }
            else {
                one = false;
            }
            if (ping.isChecked()) {
                System.out.println("Delay!");
                options += "- Delay (Ping)\n";
                one = true;
            }
            else if(!one){
                one = false;
            }
            if (jitter.isChecked()) {
                System.out.println("Jit!");
                options += "- Jitter\n";
                one = true;
            }
            else if (!one){
                one = false;
            }

            if (!one) {
                System.out.println("Defined!");
                text = "Go to Settings and choose what to test!";
            } else {
                System.out.println("Changed!\n" + options);
                text = "Tests to perform:\n" + options;
            }
        }
        else if(!one){
            System.out.println("Default!");
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);
        tv = (TextView) myView.findViewById(R.id.enabledOptions);
        start = (Button) myView.findViewById(R.id.startButton);

        System.out.println("Passou aqui!");
        doOptions();

        return myView;
    }
}
