package com.example.vincent.pokebattler;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;



/**
 * Dit bestand verzorgt het game-element in de app
 */

public class HighScore extends ListFragment {
    // Hier worden de benodigde variabelen geladen.
    private HighScoreAdapter HighScoreadap;

    public HighScore() {
    }

    // Dit maak de fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_high_score, container, false);
    }    // Hiermee wordt de informatie dialogs van een pokemon opgeroepen


    // Dit maakt de highscore aan
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Hier zetten we de Bottom navigation tijdelijk uit om te voorkomen dat de gebruiker
        *  het laden onderbreekt */
        final BottomNavigationView navigation =
                (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.INVISIBLE);
        checkinternet();
        // Hier wordt firebase aangeroepen om de informatie over de Users op te halen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase = database.getReference("Userinfo");
        nDatabase.orderByChild("HighScore").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> notes = new ArrayList<User>();

                // Voor elke User de benodigde info opgehaald
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User Users = noteDataSnapshot.getValue(User.class);
                    notes.add(Users);
                }
                // Hier wordt de Arraylist omgedraaid om een highscore te creëren
                Collections.reverse(notes);

                // Hier wordt de list gemaakt
                HighScoreadap = new HighScoreAdapter(getContext(), 1, notes);
                makeList(HighScoreadap);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }
        );
    }

    // Hier wordt de lijst gemaakt
    public void makeList(HighScoreAdapter highScoreadap) {
        // Hier wordt de Bottom Navigation weer aangezet
        final BottomNavigationView navigation =
                (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.VISIBLE);

        // Hier wordt de loading screen weggehaald en de highscore weergegeven
        ProgressBar bar = getView().findViewById(R.id.LoadingBar);
        bar.setVisibility(View.GONE);
        this.setListAdapter(highScoreadap);
        getListView().setOnItemClickListener(new ShowDetails());

    }

    private class ShowDetails implements AdapterView.OnItemClickListener {
        @Override
        //makes a onItemClick event to add a meal to your order
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Context context = getContext();
            TextView name = view.findViewById(R.id.UserName);
            TextView HighScore = view.findViewById(R.id.Score);
            float score = Float.parseFloat(HighScore.getText()
                    .toString().replace(",","."));
            String text = name.getText().toString();
            openDialog(text,score);
        }}

    // Hiermee wordt de informatie dialogs van een pokemon opgeroepen
    public void openDialog(String no,float HighScore) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        UserInfoDialog fragment3 = new UserInfoDialog().newInstance(no, HighScore);
        fragment3.show(ft, "dialog");
    }
    // dit kijkt of er internet is
    public void checkinternet() {

        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Toast.makeText(getContext(), R.string.noInternet,
                    Toast.LENGTH_SHORT).show();
        }

    }
}
