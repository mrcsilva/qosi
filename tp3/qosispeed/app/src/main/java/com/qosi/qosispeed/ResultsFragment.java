package com.qosi.qosispeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsFragment extends Fragment {

    View myView;
    private String bandwidth = "";
    private String delay = "";
    private String jitter = "";

    public void setResults(String bandwidth, boolean delay, boolean jitter, String result) {
        this.bandwidth = bandwidth;
        if(delay) {
            String[] splitted = result.split("/");
            this.delay = splitted[1] + " ms";
            if(jitter) {
                this.jitter = splitted[3];
            }
        }
        else if(jitter) {
            String[] splitted = result.split("/");
            this.jitter = splitted[3];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.results_layout, container, false);

        LinearLayout down = myView.findViewById(R.id.download_layout);
        LinearLayout up = myView.findViewById(R.id.upload_layout);
        LinearLayout del = myView.findViewById(R.id.delay_layout);
        LinearLayout jit = myView.findViewById(R.id.jitter_layout);

        View band_del = myView.findViewById(R.id.band_delay_div);
        View del_jit = myView.findViewById(R.id.delay_jit_div);

        if(bandwidth.equals("")) {
            down.setVisibility(View.GONE);
            up.setVisibility(View.GONE);
            band_del.setVisibility(View.GONE);
        }
        else {
            TextView downRes = (TextView) down.findViewById(R.id.download_result);
            TextView upRes = (TextView) up.findViewById(R.id.upload_result);
            String[] res = bandwidth.split(";");
            downRes.setText(res[0]);
            upRes.setText(res[1]);
        }
        if(delay.equals("")) {
            del.setVisibility(View.GONE);
            del_jit.setVisibility(View.GONE);
        }
        else {
            TextView delRes = (TextView) del.findViewById(R.id.delay_result);
            delRes.setText(delay);
        }
        if(jitter.equals("")) {
            if(!delay.equals("")) {
                del_jit.setVisibility(View.GONE);
            }
            jit.setVisibility(View.GONE);
        }
        else {
            TextView jitRes = (TextView) jit.findViewById(R.id.jitter_result);
            jitRes.setText(jitter);
        }

        return myView;
    }
}
