package com.example.casodistudiomamange.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.QRCodeActivity;
import com.example.casodistudiomamange.activity.SwitchLoginSignupGuestActivity;


public class GuestFragment extends Fragment {

    private Button unirsiTavolo;
    private EditText tw_username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_guest, container, false);

        unirsiTavolo = root.findViewById(R.id.uniscitiGroupOrder);
        tw_username=root.findViewById(R.id.username);

        unirsiTavolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uniscitiAlTavolo();
            }
        });

        return root;
    }

    private void uniscitiAlTavolo(){
        Intent intent= new Intent(getActivity(),QRCodeActivity.class);
        intent.putExtra("UsernameInserito",tw_username.getText().toString());
        startActivity(intent);
    }
}