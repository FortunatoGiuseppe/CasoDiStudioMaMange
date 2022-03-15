package com.example.casodistudiomamange.model;

import android.content.Context;
import android.view.View;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.ConfirmActivity;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/*Classe che gestisce il file testuale temporaneo utilizzato per salvare l'ultimo ordine e per inviare il messaggio contenente i piatti ordinati*/
public class FileOrderManager {

    public FileOrderManager() {
    }

    //Metodo per salvare i piatti dell'ultimo ordine effettuato
    public void savePlatesLastOrder(View v, ArrayList<SoPlate> soPlateParam, Context context, String FILE_NAME) {

        String text="Nessun Piatto Aggiunto";   //Stringa di default se non ci sono piatti
        for(int i=0;i<soPlateParam.size();i++){
            if(i==0){
                text="";    //se ci sono piatti allora pulisco la stringa perchè dovrà contenere la lista dei piatti
            }
            text = text+soPlateParam.get(i).getNomePiatto()+","+soPlateParam.get(i).getQuantita()+"\n";
        }

        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
            fos.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Metodo per caricare i piatti dell'ultimo ordine effettuato, li aggiunge al DB e alla lista dalla quale l'adapter prende i dati per stamparli
    public void loadPlateLastOrder(MaMangeNavigationActivity activity, String FILE_NAME, ArrayList<SoPlate> soPlate) {

        String codiceSingleOrder = activity.codiceSingleOrder;
        String codiceGroupOrder = activity.codiceGroupOrder;
        String codiceTavolo = activity.codiceTavolo;
        String username = activity.username;

        FileInputStream fis = null;

        try {
            fis = activity.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                text=text+("/");    //aggiungo lo slash per identificare la fine della riga
                SoPlate plateOrdered= new SoPlate();
                plateOrdered.setNomePiatto(text.substring(0, text.indexOf(",")));   //seleziono nomepiatto e lo metto nell'oggetto
                plateOrdered.setQuantita(Long.parseLong(text.substring(text.indexOf(",")+1, text.indexOf("/")))); //seleziono quantità

                soPlate.add(plateOrdered);   //aggiungo il piatto appena letto alla lista dei piatti da stampare

                //aggiungi piatto ordinato al db
                //se il piatto non esiste già nell'ordine dell'utente lo aggiungo
                if(!activity.dbc.checkIfPlateHasAlreadyBeenOrdered(plateOrdered.getNomePiatto(), codiceSingleOrder, codiceGroupOrder, codiceTavolo, username)){
                    activity.dbc.orderPlate(plateOrdered.getNomePiatto(), codiceSingleOrder, codiceGroupOrder, codiceTavolo, username,plateOrdered.getQuantita());
                }
            }
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
    }

    //Metodo per caricare i piatti dell'ultimo ordine effettuato per mandare l'elenco come messaggio
    public String loadPlatesOrderedFromFileForMessage(String FILE_NAME, ConfirmActivity activity) {

        FileInputStream fis = null;

        try {
            fis = activity.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String msg ;
            String text= activity.getString(R.string.inizioMessaggio);  //inizio del messaggio
            text=text+("\n");
            msg=text;

            while ((text = br.readLine()) != null) {
                msg=msg+text+("\n");                    //costruzione della stringa tramite letture di ogni singola riga dal file
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
