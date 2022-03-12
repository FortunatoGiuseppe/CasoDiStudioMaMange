package com.example.casodistudiomamange.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfirmFragment extends Fragment {

    ImageView quiz,share;
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
        quiz=v.findViewById(R.id.QuizImg);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= new QuestionFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        });

        share = v.findViewById(R.id.ShareImg);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //devo chiedere in che app vuole condividere (whatsapp), scegliere persona, nella chat caricare
                //come messaggio l'ordine che ha fatto

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, loadPlatesOrderedFromFileForMessage());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });

        return v;
    }

    //Metodo per caricare i piatti dell'ultimo ordine effettuato per mandare l'elenco come messaggio
    public String loadPlatesOrderedFromFileForMessage() {

        FileInputStream fis = null;

        try {
            fis = ( getActivity()).openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String msg ;
            String text= getResources().getString(R.string.inizioMessaggio);
            text=text+("\n");
            msg=text;

            while ((text = br.readLine()) != null) {
               msg=msg+text+("\n");    //aggiungo lo slash per identificare la fine della riga
            }
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}