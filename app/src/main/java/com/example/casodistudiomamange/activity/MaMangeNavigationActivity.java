package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import com.example.casodistudiomamange.fragment.GroupOrderFragment;
import com.example.casodistudiomamange.fragment.RestaurantFragment;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.example.casodistudiomamange.model.DatabaseController;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Activity che contiene:
 * - la BottomNavigationView che contiene le funzionalit√† come profilo, carica ultimo ordine
 * - la NavigationView per navigare tra le schede menu, single order e group order
 * - fragment relativi alle schede sopra citate
 */
public class MaMangeNavigationActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{
    public DatabaseController dbc;  //istanza per utilizzare metodi relativi al DB
    public TextView topBarTitle;
    BottomNavigationView bottomNavigationView;  //riferimento utilizzato per accedere alla barra in alto con le 3 linee orizzontali
    public String username;
    public String codiceTavolo;
    private FirebaseAuth lAuth;

    NetworkChangedListener networkChangedListener = new NetworkChangedListener();
    public String codiceSingleOrder;
    public String codiceGroupOrder;

    //elementi del menu che compare quando si clicca sulle 3 linette
    private MenuItem profileItem;
    public MenuItem lastOrderItem;

    private int countClicksOnBackButton = 0;

    public static final String SHARED_PREFS = "sharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ma_mange_navigation);
        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        topBarTitle =findViewById(R.id.topBarTitle);

        lAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        username= intent.getStringExtra("UsernameInserito");

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        lastOrderItem = navigationView.getMenu().findItem(R.id.lastOrder);

        String email = getEmail();
        if (email.equals("Guest")) {
            //Se l'utente √® ospite devo disabilitare i tasti profilo e caricamento ultimo ordine perch√© solo per utenti registrati
            profileItem = navigationView.getMenu().findItem(R.id.menuProfile);
            profileItem.setEnabled(false);
            profileItem.getIcon().setAlpha(130);

            lastOrderItem.setEnabled(false);
            lastOrderItem.getIcon().setAlpha(130);

        }
        clearSharedPreferencesQuantities();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Support:
                        launchSupportActivity();
                        break;

                    case R.id.menuProfile:
                        launchProfileActivity(username);
                        break;

                    case R.id.lastOrder:
                        launchLastOrderFragment();

                        lastOrderItem.setEnabled(false);
                        lastOrderItem.getIcon().setAlpha(130);
                        break;

                    case R.id.About:
                        launchAboutActivity();
                        break;
                }
                return false;
            }
        });
        intent = getIntent();
        codiceTavolo =intent.getStringExtra("CodiceTavolo");

        /*Creazione dell'oggetto dbc il quale permette la manipolazione dei dati con il database*/
        dbc = new DatabaseController();
        dbc.createOrdersFirestore(codiceTavolo, new DatabaseController.metododiCallback() {
            @Override
            //metodo per assegnare pubblicamente il singleOrder e groupOrder letto da DatabaseController
            public void onCallback(String codiceSingleOrderCheMiServe, String codiceGroupOrderCheMiServe) {
                codiceSingleOrder = codiceSingleOrderCheMiServe;
                codiceGroupOrder = codiceGroupOrderCheMiServe;
                username = username+" "+codiceSingleOrder;
            }
        });

        Fragment fragment;
        fragment = new RestaurantFragment();
        loadFragment(fragment);

        bottomNavigationView.setOnItemSelectedListener(this);
        showBadge(0);
    }

    /**
     * Metodo usato per convertire da dp a pixel, utile per il badge
     * @param context
     * @param dp
     * @return px ottenuti dalla conversione
     */
    public static int dpToPx(Context context, int dp){
        Resources resources=context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, resources.getDisplayMetrics()));

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public Fragment fragment = null;

    /**Metodo per la navigazione tra gli item della bottomNavigationBar**/
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.restaurant_menu:
                fragment = new RestaurantFragment();
                break;
            case R.id.single_order:
                fragment = new SingleOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putString("chiamante", "tastoSingleOrder"); //specifica a singleOrderFragment che deve caricare l'ordine corrente
                fragment.setArguments(bundle);
                break;
            case R.id.group_order:
                fragment = new GroupOrderFragment();
                break;

        }
        return loadFragment(fragment);
    }

    /**
     * metodo che permette di caricare il fragment passato come parametro
     * @param fragment da caricare
     * @return sempre true, richiesto perch√© onNavigationItemSelected √® di tipo boolean e non si pu√≤ cambiare la firma
     */
    private boolean loadFragment(Fragment fragment) {
        // prendo la lista dei fragment attivi
        List<Fragment> allCurrentFragments=getSupportFragmentManager().getFragments();

        //se non ho fragment attivi allora √® la prima volta che viene chiamata questa activity, quindi carico sicuramente il fragment passato
        if(allCurrentFragments.size()==0){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }else {

            //altrimenti prendo ultimo fragment attivo (cio√® quello visibile a schermo)
            Fragment currentFragment = allCurrentFragments.get(allCurrentFragments.size() - 1);
            // e controllo che non sia uguale a quello che l'utente vuole vedere
            if (fragment != null && currentFragment.getClass() != fragment.getClass()) {
                //se fragment attivo e quello che l'utente vuole vedere sono diversi allora chiamo quello cercato dall'utente altrimenti non faccio niente
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
            }

            if (currentFragment.getClass()==SingleOrderFragment.class){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
            }
        }
        return true;
    }


    @Override
    public void onBackPressed() {

        // prendo la lista dei fragment attivi
        List<Fragment> allCurrentFragments=getSupportFragmentManager().getFragments();
        Fragment currentFragment = allCurrentFragments.get(allCurrentFragments.size() - 1);

        //Una volta entrato nella sezione men√Ļ non √® pi√Ļ possibile tornare indietro alla selezione del tavolo
        if(bottomNavigationView.getSelectedItemId()==R.id.restaurant_menu){
            if(currentFragment.getClass()== RestaurantFragment.class) { //solo se sono nel fragment di visualizzazione categorie del menu
                countClicksOnBackButton++;
            }
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
        }


        //Una volta entrato nella sezione del singolo ordine o dell'ordine collettivo nel momento in cui clicco sul tasto "Indietro" sono reindirizzato al men√Ļ
        if((bottomNavigationView.getSelectedItemId()==R.id.single_order) || (bottomNavigationView.getSelectedItemId()==R.id.group_order)){
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
        }


        //Una volta entrato nella categoria di un piatto nel momento in cui clicco "Indietro" vengo riendirizzato alla sezione men√Ļ
        if(bottomNavigationView.getSelectedItemId()==R.id.recycleview_plates){
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
        }



        //Se clicco due volte sul tasto back allora √® probabile che si vuole chiudere l'app
        if(countClicksOnBackButton > 1){

            AlertDialog.Builder builder = new AlertDialog.Builder(this
            );
            builder.setTitle(getText(R.string.uscire));
            builder.setMessage(getText(R.string.uscireMsg));
            builder.setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dbc.deleteAllDataOfUser(codiceTavolo,codiceGroupOrder,codiceSingleOrder,username); //quando l'utente esce cancello dal db tutto ci√≤ che aveva fatto
                    //imposto i valori a null per non lasciare traccia nell'app di ci√≤ che si era fatto prima
                    username = null;
                    codiceGroupOrder = null;
                    codiceSingleOrder = null;
                    codiceTavolo = null;

                    finishAffinity(); //elimina tutto lo stack delle activity attive
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    countClicksOnBackButton = 0;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private void launchSupportActivity(){
        Intent intent1 = new Intent(MaMangeNavigationActivity.this, SupportActivity.class);
        startActivity(intent1);
    }

    private void launchProfileActivity(String username){
        Intent intent2 = new Intent(MaMangeNavigationActivity.this, ProfileActivity.class);
        intent2.putExtra("username",username);
        startActivity(intent2);
    }

    private void launchAboutActivity(){
        Intent intent3 = new Intent(MaMangeNavigationActivity.this, AboutActivity.class);
        startActivity(intent3);
    }

    private void launchLastOrderFragment(){

        //carica dati dal file e apri single order con i piatti presenti nel file
        Fragment fragment = new SingleOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chiamante", "lastOrder"); //specifica in singleOrderFragment che deve caricare l'ultimo ordine salvato
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    private String getEmail(){
        String guest = "Guest";
        if(lAuth.getCurrentUser()!=null){
            return lAuth.getCurrentUser().getEmail();
        }
        return guest;
    }

    @Override
    protected void onStart(){
        //verifica della presenza della connessione internet
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        //verifica della presenza della connessione internet
        unregisterReceiver(networkChangedListener);
        super.onStop();
    }

    /**
     * metodo per salvare nello shared preferences la quantit√† relativa al piatto passato come parametro
     * @param nomePiatto nome del piatto di cui si vuole salvare la quantit√† nello shared preference
     * @param total la quantit√† da salvare
     */
    public void saveDataSharedPreferences(String nomePiatto, int total) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(nomePiatto,total);   //salvataggio nello shared preference del piatto la quantit√†
        editor.apply();
    }

    /**
     * metodo per caricare dallo shared preferences la quantit√† relativa al piatto passato come parametro
     * @param nomePiatto nome del piatto del quale si √® interessati a sapere la quantit√†
     */
    public int getQuantityForParameterPlateSharedPreferences(String nomePiatto) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //0 √® il valore passato di default, cio√® se nello shared preferences non esiste una quantit√† precedentemente aggiunta per quel piatto
        return sharedPreferences.getInt(nomePiatto,0);
    }

    /**
     * metodo per svuotare lo shared preferences
     */
    public void clearSharedPreferencesQuantities() {
        SharedPreferences sharedPreferences =  this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Metodo che permette di mostrare il numero dei piatti aggiunti all'ordine
     * @param numberToShow numero dei piatti da mostrare
     */
    public void showBadge(int numberToShow) {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.single_order);
        if(numberToShow!=0){
            badge.setVisible(true);
            badge.setVerticalOffset(dpToPx(MaMangeNavigationActivity.this,3));
            badge.setNumber(numberToShow);
            badge.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            badge.setBadgeTextColor(getResources().getColor(R.color.white));
        }else{
            badge.setVisible(false);
        }
    }

    /**
     * Metodo che permette di aggiornare la quantit√† mostrata prendendo il numero dallo shared preferences
     * Viene usato negli adapter perch√© l√¨ non ho la lista intera, allora posso leggerla dallo shared
     */
    public void updateQuantityOnBadge() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        //Dal conteggio dei piatti nello shared preferences devo togliere tutti i piatti che hanno quantit√† =0
        //questi piatti erano stati aggiunti e poi rimossi dall'ordine, non devo quindi contarli

        Map<String, ?> savedPlates= sharedPreferences.getAll();
        Collection<?> values =savedPlates.values();
        while (values.contains(0)){
            values.remove(0);
        }
        showBadge(values.size()); //chiamo il metodo per mostare il numero
    }
}