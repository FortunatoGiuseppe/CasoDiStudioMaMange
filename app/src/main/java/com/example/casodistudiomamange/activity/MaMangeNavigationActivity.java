package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.fragment.GroupOrderFragment;
import com.example.casodistudiomamange.fragment.RestaurantFragment;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.example.casodistudiomamange.model.DatabaseController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class MaMangeNavigationActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{
    public DatabaseController dbc;
    BottomNavigationView bottomNavigationView;
    public String username;
    public String codiceTavolo;
    private FirebaseAuth lAuth;

    public String codiceSingleOrder;
    public String codiceGroupOrder;

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

        String email = getEmail();
        if (email.equals("Guest")) {
            navigationView.getMenu().removeItem(R.id.menuProfile);
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
                bundle.putString("chiamante", "tastoSingleOrder");
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
        bundle.putString("chiamante", "lastOrder");
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
}