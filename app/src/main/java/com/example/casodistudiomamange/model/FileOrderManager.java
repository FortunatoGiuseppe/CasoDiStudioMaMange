package com.example.casodistudiomamange.model;

import android.content.Context;
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
import java.util.Map;
import java.util.Objects;


/**
 * Classe che gestisce il file testuale temporaneo utilizzato per salvare l'ultimo ordine e
 * per inviare il messaggio contenente i piatti ordinati
 */
public class FileOrderManager {

    public FileOrderManager() {
    }

    /**
     * Metodo per salvare i piatti dell'ultimo ordine effettuato nel file txt
     * @param soPlateParam lista degli soPlate da salvare nel file
     * @param context contesto necessario per l'apertura del file
     * @param FILE_NAME nome del file
     */
    public void savePlatesLastOrder(ArrayList<SoPlate> soPlateParam, Context context, String FILE_NAME) {

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


    /**
     * Metodo per caricare i piatti dell'ultimo ordine effettuato, li aggiunge al DB e alla lista dalla quale l'adapter prende i dati per stamparli
     * @param activity necessaria per aprire il file
     * @param FILE_NAME nome del file
     * @param soPlate conterrà gli soPlate appena letti e creati
     */
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
                int firstIndex=text.indexOf(",");
                int secondIndex=text.indexOf("/");

                if(firstIndex<0 || secondIndex<0){
                    //Se non ho trovato gli indici allora non era mai stato salvato un single order, quindi non carico plateOrdered

                }else{
                    plateOrdered.setNomePiatto(text.substring(0, firstIndex));   //seleziono nomepiatto e lo metto nell'oggetto
                    plateOrdered.setQuantita(Long.parseLong(text.substring(firstIndex+1, secondIndex))); //seleziono quantità
                    soPlate.add(plateOrdered);   //aggiungo il piatto appena letto alla lista dei piatti da stampare
                }

                //aggiungi piatto ordinato al db
                if(!Objects.isNull(plateOrdered) ){
                    activity.dbc.orderPlate(plateOrdered.getNomePiatto(), codiceSingleOrder, codiceGroupOrder, codiceTavolo, username, plateOrdered.getQuantita());
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


    /**
     * Metodo per caricare i piatti dell'ultimo ordine effettuato per mandare l'elenco come messaggio
     * @param FILE_NAME nome del file dal quale leggere
     * @param activity contesto necessario per aprire il file
     * @return la stringa contenente tutti i piatti e relativa quantità
     */
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
                int firstIndex=text.indexOf(",");
                int secondIndex=text.length();
                msg=msg+text.substring(firstIndex+1, secondIndex)+" "+activity.getString(R.string.piattiDi)+" "
                        +text.substring(0, firstIndex)+("\n");
                //costruzione della stringa tramite letture di ogni singola riga dal file
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


    /**
     * Metodo che prende i dati del parametro e li organizza in un unica stringa (che verrà inserita nel file per essere caricata in Storage Database)
     * Serve solo a creare l'impostazione del file che viene effettivamente creato e caricato nel metodo chiamante
     * @param soPlateParam lista dei piatti ordinati
     * @return una stringa contenente diverse righe con username, nome del piatto e relativ quantità
     */
    public String saveGroupOrderForKitchen(ArrayList<SoPlate> soPlateParam) {
        String text="Nessun Piatto Aggiunto";   //Stringa di default se non ci sono piatti
        for(int i=0;i<soPlateParam.size();i++){
            if(i==0){
                text="";    //se ci sono piatti allora pulisco la stringa perchè dovrà contenere la lista dei piatti
            }
            text = text+soPlateParam.get(i).getUsername()+","+soPlateParam.get(i).getNomePiatto()+","+soPlateParam.get(i).getQuantita()+"\n";
        }
        return  text;
    }



    /**
     * Metodo per leggere le quantità ed i piatti inseriti in un file dell'ultimo ordine salvato e caricare una mappa.
     * Serve per caricare le giuste quantità nella schermata del menu (categoryFragment)
     * @param activity necessaria per aprire il file locale
     * @param FILE_NAME il nome del file locale
     * @param map contiene nome del piatto e quantità relativa ordinata
     */
    public void loadQuantitiesFromFile (MaMangeNavigationActivity activity, String FILE_NAME, Map<String,Long> map){

        FileInputStream fis = null;

        try {
            fis = activity.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                text=text+("/");    //aggiungo lo slash per identificare la fine della riga
                int firstIndex=text.indexOf(",");
                int secondIndex=text.indexOf("/");
                if(firstIndex<0 || secondIndex<0){
                    //Se non ho trovato gli indici allora non era mai stato salvato un single order, quindi non carico la mappa
                }else {
                    map.put(text.substring(0, firstIndex), Long.parseLong(text.substring(firstIndex + 1, secondIndex)));   //seleziono nomepiatto e quantità
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
}
