package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.fragment.GroupOrderFragment;
import com.example.casodistudiomamange.fragment.RestaurantFragment;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.example.casodistudiomamange.model.DatabaseController;
import com.example.casodistudiomamange.model.Table;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MaMangeNavigationActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{
    public String codiceSingleOrder;
    public String codiceGroupOrder;
    public DatabaseController dbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ma_mange_navigation);

        Intent intent = getIntent();
        String usernameInserito = intent.getStringExtra("UsernameInserito");

        dbc = new DatabaseController();

        /*codiceSingleOrder=dbc.codiceSingleOrder;
        codiceGroupOrder=dbc.codiceGroupOrder;*/

        dbc.dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Table table = dataSnapshot.getValue(Table.class);
                    if(table.getCodicetavolo().equals("MST001")) {
                        if (!groupOrderExists(table)){
                            dbc.createOrders(usernameInserito);
                        }else{
                            dbc.joinOrder(usernameInserito);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        Fragment fragment = null;
        fragment = new RestaurantFragment();
        loadFragment(fragment);

        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation_bar);
        navigationBarView.setOnItemSelectedListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.restaurant_menu:
                fragment = new RestaurantFragment();
                break;
            case R.id.single_order:
                fragment = new SingleOrderFragment();
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

    private boolean groupOrderExists(Table table){
        return table.getFlag()==1;
    }

}