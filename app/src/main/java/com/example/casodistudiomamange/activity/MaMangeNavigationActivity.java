package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.fragment.GroupOrderFragment;
import com.example.casodistudiomamange.fragment.MenuFragment;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MaMangeNavigationActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ma_mange_navigation);
        Fragment fragment = null;
        fragment = new MenuFragment();
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
                fragment = new MenuFragment();
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
}