package com.example.vincent.pokebattler;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Dit bestand slaat de extra userinfo op
 */
public class Userinfo extends Fragment implements View.OnClickListener {

    public Userinfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        View view= inflater.inflate(R.layout.fragment_userinfo, container, false);
        FloatingActionButton Continue = view.findViewById(R.id.Send);
        Continue.setOnClickListener(this);
        return view;
    }

    // kijkt of de user is ingelogd
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent goToNextActivity = new Intent(getContext(), Authentication.class);
            startActivity(goToNextActivity);
        }
    }

    @Override
    public void onClick(View view) {

        // Hier worden de benodigde variabelen opgehaald
        EditText Name = getView().findViewById(R.id.Username);
        EditText Age = getView().findViewById(R.id.Age);
        EditText Gen = getView().findViewById(R.id.Gen);

        // Hier wordt alle tekst opgehaald
        String sName = Name.getText().toString();
        String sAge = Age.getText().toString();
        String sGen = Gen.getText().toString();

        // Hier wordt gekeken of de velden niet leeg zijn
        if (!Objects.equals(sName, "") & !Objects.equals(sAge, "")
            & !Objects.equals(sGen, "")){
            sendInfo(sName,sAge,sGen);
            Intent goToNextActivity = new Intent(getContext(), MainActivity.class);
            startActivity(goToNextActivity);
            getActivity().finish();
        }
    }

    // Hier wordt de userinfo opgeslagen
    public void sendInfo(final String Name, final String Age, final String Gen){
        // Hier worden de benodigde variabelen voorbereid
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final ArrayList<pokemon> pokemons = new ArrayList<pokemon>();

        // Hier wordt firebase aangeroepen om informatie toe te schrijven
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        // We openen hier pokemon om pikachu als standaard favoriet in te stellen
        DatabaseReference nDatabase1= database1.getReference("Pokemon");
        nDatabase1.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot noteDataSnapshot1 : dataSnapshot1.getChildren()) {
                    pokemon Pokemons2 = noteDataSnapshot1.getValue(pokemon.class);
                    if (Objects.equals(Pokemons2.Name, "Pikachu")) {
                        pokemons.add(Pokemons2);

                        // Hier wordt de informatie geplaatst
                        User userInfo = new User(Name,Age, pokemons,Gen);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Userinfo/"+user.getUid(),userInfo);
                        database.updateChildren(childUpdates);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }
        );
    }
}
