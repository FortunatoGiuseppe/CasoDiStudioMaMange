package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import com.example.casodistudiomamange.fragment.GroupOrderFragment;
import com.example.casodistudiomamange.fragment.RestaurantFragment;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.example.casodistudiomamange.model.DatabaseController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class MaMangeNavigationActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{
    public DatabaseController dbc;  //istanza per utilizzare metodi relativi al DB
    BottomNavigationView bottomNavigationView;  //riferimento utilizzato per accedere alla barra in alto con le 3 linee orizzontali
    public String username;
    public String codiceTavolo;
    private FirebaseAuth lAuth;
    NetworkChangedListener networkChangedListener = new NetworkChangedListener();
    public String codiceSingleOrder;
    public String codiceGroupOrder;

    //elementi del menu che compae quando clicchi sulle 3 linette
    private MenuItem profileItem;
    public MenuItem lastOrderItem;
    private MenuItem acc_regItem;

    private boolean hasLastOrderBeenClicked=false; //flag che consente di caricare solo una volta l'ordine precedente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ma_mange_navigation);
        Objects.requireNonNull(getSupportActionBar()).hide();
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
            //Se l'utente è ospite devo disabilitare i tasti profilo e caricamento ultimo ordine perché solo per utenti registrati
            profileItem = navigationView.getMenu().findItem(R.id.menuProfile);
            profileItem.setEnabled(false);
            profileItem.getIcon().setAlpha(130);

            lastOrderItem.setEnabled(false);
            lastOrderItem.getIcon().setAlpha(130);

            acc_regItem = navigationView.getMenu().findItem(R.id.acc_reg);
            acc_regItem.setVisible(true);   //rendo visibile la scritta che suggerisce di loggarsi per accedere a profilo e caricare ultimo ordine
            acc_regItem.setEnabled(false);
            acc_regItem.getIcon().setAlpha(130);

        }

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
                }
                return false;
            }
        });
        intent = getIntent();
        codiceTavolo =intent.getStringExtra("CodiceTavolo");

        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);

        dbc = new DatabaseController();
        dbc.createOrdersFirestore(username, codiceTavolo, new DatabaseController.metododiCallback() {
            @Override
            //metodo per assegnare pubblicamente il singleOrder e groupOrder letto da DatabaseController
            public void onCallback(String codiceSingleOrderCheMiServe, String codiceGroupOrderCheMiServe) {
                codiceSingleOrder = codiceSingleOrderCheMiServe;
                codiceGroupOrder = codiceGroupOrderCheMiServe;
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();

        Fragment fragment;
        fragment = new RestaurantFragment();
        loadFragment(fragment);

        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public Fragment fragment = null;
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

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {

        //Una volta entrato nella sezione menù non è più possibile tornare indietro alla selezione del tavolo
        if(bottomNavigationView.getSelectedItemId()==R.id.restaurant_menu){
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
        }


        //Una volta entrato nella sezione del singolo ordine o dell'ordine collettivo nel momento in cui clicco sul tasto "Indietro" sono reindirizzato al menù
        if((bottomNavigationView.getSelectedItemId()==R.id.single_order) || (bottomNavigationView.getSelectedItemId()==R.id.group_order)){
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
        }


        //Una volta entrato nella categoria di un piatto nel momento in cui clicco "Indietro" vengo riendirizzato alla sezione menù
        if(bottomNavigationView.getSelectedItemId()==R.id.recycleview_plates){
            bottomNavigationView.setSelectedItemId(R.id.restaurant_menu);
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
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangedListener);
        super.onStop();
    }
}