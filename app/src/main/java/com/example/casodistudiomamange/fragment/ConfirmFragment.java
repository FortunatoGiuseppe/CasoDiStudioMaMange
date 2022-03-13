package com.example.casodistudiomamange.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.model.FileOrderManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfirmFragment extends Fragment {


    ImageView quiz,share;
    View quizCostraint,shareCostarint;
    ConstraintSet.Constraint shareCostraint;
    private static final String FILE_NAME = "lastOrder.txt";

    public ConfirmFragment() {
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
        View v=inflater.inflate(R.layout.fragment_confirm, container, false);
        quizCostraint=v.findViewById(R.id.QuizConstraint);
        quiz=v.findViewById(R.id.QuizImg);
        quizCostraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= new QuestionFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        });
        shareCostarint=v.findViewById(R.id.Shareconstraint);
        share = v.findViewById(R.id.ShareImg);
        shareCostarint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //devo chiedere in che app vuole condividere (whatsapp), scegliere persona, nella chat caricare
                //come messaggio l'ordine che ha fatto
                FileOrderManager fileOrderManager= new FileOrderManager();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, fileOrderManager.loadPlatesOrderedFromFileForMessage(FILE_NAME,(MaMangeNavigationActivity) getActivity()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });

        return v;
    }



}