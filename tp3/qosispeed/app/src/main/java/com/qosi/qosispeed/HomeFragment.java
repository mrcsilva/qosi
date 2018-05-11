package com.qosi.qosispeed;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    View myView;
    private SettingsFragment sf = null;
    private TextView tv;
    private Button start;
    private boolean one;
    private String text = "";
    private FragmentManager fm = null;
    private ProgressBar p;
    private ActionBar actionBar = null;


    public void setSettings(SettingsFragment sf) {
        this.sf = sf;
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
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

    private void doPing() {
        for(int i = 0; i < 1000000; i++);
    }

    private void startTest() {
        p.setVisibility(View.VISIBLE);

        doPing();

        ResultsFragment results = new ResultsFragment();
        String ping = "22 ms";
        results.setResults(null, ping, null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, results).commit();
        p.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);
        tv = (TextView) myView.findViewById(R.id.enabledOptions);
        start = (Button) myView.findViewById(R.id.startButton);
        p = (ProgressBar) myView.findViewById(R.id.wait_results);

        p.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor("#3f51b5")));
        p.setVisibility(View.INVISIBLE);


        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v == myView.findViewById(R.id.startButton)) {
                    startTest();
                }
            }
        });

        doOptions();

        return myView;
    }
}
