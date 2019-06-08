package com.gorun.kelompok7.runningapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gorun.kelompok7.runningapp.Fragment.DashboardFragment;
import com.gorun.kelompok7.runningapp.Fragment.HomeFragment;
import com.gorun.kelompok7.runningapp.Fragment.ProfileFragment;
import com.gorun.kelompok7.runningapp.LoginActivity;
import com.gorun.kelompok7.runningapp.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    Fragment fragment;
    public boolean isTiming;
    public static double BERAT, TINGGI;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    public static String ID_USER;
    public static String NAMA_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mAuth == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.activity_main);

        ID_USER = mUser.getUid();
        NAMA_USER = mUser.getDisplayName();

        // loading the default fragment
        loadFragment(new HomeFragment());

        // getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        isTiming = new HomeFragment().getCurrentlyTimming();

        BERAT = getIntent().getDoubleExtra("berat", 30);
        TINGGI = getIntent().getDoubleExtra("tinggi", 100);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragment = null;
        switch (item.getItemId()){
            case R.id.navigation_home :
                fragment = new HomeFragment();
                break;

            case R.id.navigation_dashboard :
                fragment = new DashboardFragment();
                break;

            case R.id.navigation_profile :
                fragment = new ProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    public void setActionBar(String title) {
        getSupportActionBar().setTitle(title);
    }
}
