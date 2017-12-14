package com.example.vincent.pokebattler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Dit bestand is de main activity van de app en dient als frame voor alle functionaliteiten
 */
public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            // Hier wordt gekeken welke button in de bottom navigation is aangeklikt
            switch (item.getItemId()) {

                // in elke case wordt de bijbehorende fragment geopend
                case R.id.navigation_play:
                    FragmentManager fm = getSupportFragmentManager();
                    StartingFragment fragment = new StartingFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.Fragment_container, fragment);
                    ft.commit();
                    return true;
                case R.id.navigation_highscores:
                    FragmentManager fm3 = getSupportFragmentManager();
                    HighScore fragment3 = new HighScore();
                    FragmentTransaction ft3 = fm3.beginTransaction();
                    ft3.replace(R.id.Fragment_container, fragment3);
                    ft3.commit();
                    return true;
                case R.id.navigation_signout:
                    // Hier kan de user uitloggen
                    SignOut();
                    return false;
                case R.id.navigation_pokedex:
                    FragmentManager fm2 = getSupportFragmentManager();
                    PokeDex fragment2 = new PokeDex();
                    FragmentTransaction ft2 = fm2.beginTransaction();
                    ft2.replace(R.id.Fragment_container, fragment2);
                    ft2.commit();
                    return true;
                case R.id.navigation_train:
                    FragmentManager fm1 = getSupportFragmentManager();
                    BattleScreen fragment1 = new BattleScreen();
                    FragmentTransaction ft1 = fm1.beginTransaction();
                    ft1.replace(R.id.Fragment_container, fragment1);
                    ft1.commit();
                    return true;
            }
            return false;
        }
    };

    // Hier wordt de activity aangemaakt en de start Fragment geopend
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Als je terug klikt in de register/inlog activity, dan sluit de app meteen
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_main);

        // Open de main fragment
        FragmentManager fm = getSupportFragmentManager();
        StartingFragment fragment = new StartingFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Fragment_container, fragment);
        ft.commit();
        // Hier zetten we de OnClickListener voor de bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    // Check of de user ingelogd is
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent goToNextActivity = new Intent(getApplicationContext(), Authentication.class);
            startActivity(goToNextActivity);
        }
    }

    // De tijd om onBackPressed te fixen
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        // Hier worden de benodigde variabelen benoemd
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        long t = System.currentTimeMillis();
        navigation.setVisibility(View.VISIBLE);
        // bij de eerste click wordt je teruggestuurd naar de homeScreen
        if (t - backPressedTime > 2000) {
            if (navigation.getSelectedItemId() != R.id.navigation_play) {
                FragmentManager fm = getSupportFragmentManager();
                StartingFragment fragment = new StartingFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.Fragment_container, fragment);
                navigation.setSelectedItemId(R.id.navigation_play);
                ft.commit();
            }// 2 secs
            backPressedTime = t;
            Toast.makeText(this, R.string.LeaveWarning,
                    Toast.LENGTH_SHORT).show();
        }

        // Kill de app
        else {
            super.onBackPressed();
        }
    }

    // Hier wordt de dialog om uit te loggen gemaakt
    public void SignOut() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage("Do you really want to leave me?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        onStart();
                    }
                });
        alertDialog.show();
    }


}
