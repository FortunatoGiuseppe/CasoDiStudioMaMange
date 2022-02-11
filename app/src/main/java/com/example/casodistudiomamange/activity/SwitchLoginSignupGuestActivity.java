package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.adapter.ViewPagerFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SwitchLoginSignupGuestActivity extends AppCompatActivity {

    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private  String[] titles = {"Guest", "Login", "Signup"};
    private String[] titles_it = {"Ospite", "Login", "Registrati"};
    private FirebaseAuth lAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_login_signup_guest);
        lAuth = FirebaseAuth.getInstance();
        viewPager2=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tab_layout);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this);

        getSupportActionBar().hide();

        viewPager2.setAdapter(viewPagerFragmentAdapter);
        if(lAuth.getCurrentUser() != null){
            Toast.makeText(this,"Logged in successfully!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoggedUser.class));
            finish();
        }

        if(Locale.getDefault().getLanguage().equals("it")){
            new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles_it[position]))).attach();
        }else{
            new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles[position]))).attach();
        }


    }
}