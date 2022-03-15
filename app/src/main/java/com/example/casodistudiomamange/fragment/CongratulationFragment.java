package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.casodistudiomamange.R;
import java.util.Random;


public class CongratulationFragment extends Fragment {

    private TextView congratulationsTv,codeTv;
    private View congcostr;


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
        congratulationsTv=v.findViewById(R.id.congratulationsTv);
        congcostr=v.findViewById(R.id.CongCostr);
        if(score.equals(max)){
            congratulationsTv.setText(R.string.vittoriaQuiz);
            codeTv=v.findViewById(R.id.CodeTv);
            codeTv.setText(getRandomString(7));
        }else{
            congratulationsTv.setText(R.string.sconfittaQuiz);
        }
        return v;
    }


    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}