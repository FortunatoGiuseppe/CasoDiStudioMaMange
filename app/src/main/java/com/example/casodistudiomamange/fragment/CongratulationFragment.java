package com.example.casodistudiomamange.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.casodistudiomamange.R;


public class CongratulationFragment extends Fragment {

    private TextView congratulations;



    public CongratulationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        String score = bundle.getString("score");
        String max=bundle.getString("max");
        View v=inflater.inflate(R.layout.fragment_congratulation, container, false);
        congratulations=v.findViewById(R.id.congratulations);
        if(score.equals(max)){
            congratulations.setText("Congratulazioni hai vinto una ciola di gomma, ritirala in cassa");
        }else{
            congratulations.setText("Ritenta la prossima volta");
        }

        return v;
    }
}