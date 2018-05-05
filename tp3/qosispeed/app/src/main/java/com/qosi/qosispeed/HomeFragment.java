package com.qosi.qosispeed;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    View myView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);


        TextView tv = (TextView) myView.findViewById(R.id.enabledOptions);
        Button start = (Button) myView.findViewById(R.id.startButton);

        if(tv.length() == 0) {
            start.setEnabled(false);
            start.setBackgroundColor(Color.parseColor("grey"));
            tv.setText("Go to Settings and choose what to test!");
        }

        return myView;
    }
}
