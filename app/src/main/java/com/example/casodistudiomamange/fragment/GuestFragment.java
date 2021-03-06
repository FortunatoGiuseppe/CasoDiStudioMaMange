package com.example.casodistudiomamange.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.QRCodeActivity;

/**
 * Fragment relativo all'utente ospite, viene chiesto username per unirsi al tavolo
 */
public class GuestFragment extends Fragment {

    private static final int MAX_LENGTH = 10;
    private EditText tw_username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_guest, container, false);
        Button unirsiTavolo = root.findViewById(R.id.uniscitiGroupOrder);
        tw_username=root.findViewById(R.id.username);

        unirsiTavolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uniscitiAlTavolo();
            }
        });
        return root;
    }

    /**
     * Metodo che richiede inserimento username all'utente
     */
    private void uniscitiAlTavolo(){
        String username_ins=tw_username.getText().toString();

        if(username_ins.length()==0 || username_ins.length()>MAX_LENGTH){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle(R.string.username_nonvalido);
            builder1.setMessage(R.string.username_nonvalido_descr);
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder1.create();
            alert.show();

        }else {
            Intent intent= new Intent(getActivity(),QRCodeActivity.class);
            username_ins = username_ins + " "+ getContext().getResources().getString(R.string.Guest);
            intent.putExtra("UsernameInserito",username_ins);
            startActivity(intent);
        }
    }
}