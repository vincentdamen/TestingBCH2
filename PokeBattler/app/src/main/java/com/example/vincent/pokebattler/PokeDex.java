package com.example.vincent.pokebattler;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PokeDex extends ListFragment {
    // Benoemt de benodigde variabelen
    private dexAdapter DexAdapter;
    public PokeDex() {
        // Required empty public constructor
    }

    // maakt je view aan
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_poke_dex, container, false);

        return view;
    }

    // Maakt je fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hier zetten we de Bottom navigation uit
        final BottomNavigationView navigation =
                (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.INVISIBLE);

        // Kijkt of er internet
        checkinternet();

        // Hier openen we firebase om de pokemons op te halen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase = database.getReference("Pokemon");
        nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<pokemon> notes = new ArrayList<pokemon>();

                // Voor elke pokemon wordt deze opgeslagen en aan de arraylist toegevoefd
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {

                    pokemon Pokemons2 = noteDataSnapshot.getValue(pokemon.class);
                    notes.add(Pokemons2);
                }
                // Hier wordt de arraylist in de adapter gezet
                DexAdapter = new dexAdapter(getContext(), 1, notes);

                // Hier wordt de list gemaakt
                makeList(DexAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}

    // Deze methode maakt de lijst
    public void makeList(dexAdapter DexAdapter) {final BottomNavigationView navigation =
        // Hier wordt het laadscherm uitgezet
        (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.VISIBLE);
        ProgressBar bar = getView().findViewById(R.id.progressBar3);
        bar.setVisibility(View.GONE);

        // Hier wordt de adapter gezet
        this.setListAdapter(DexAdapter);

        // Hier worden de onClicklisteners gezet
        getListView().setOnItemClickListener(new ShowDetails());
        getListView().setOnItemLongClickListener(new MakeFavorite());
        getListView().invalidateViews();

    }

    // Dit is de Onclicklistener om informatie te bekijken
    private class ShowDetails implements AdapterView.OnItemClickListener {
        @Override
        //makes a onItemClick event to add a meal to your order
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Context context = getContext();
            TextView textviw = view.findViewById(R.id.No);
            String text = textviw.getText().toString();

            // Hier wordt de pokemon info dialog geopend
            openDialog(text);
        }}

    // Deze class maakt de onItemLongClick voor een favoriet toe te voegen of weg te halen
    private class MakeFavorite implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view,
                                       final int i, long l) {

            // hier wordt firebase geopend om de favorieten op te halen
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference nDatabase = database.getReference("Userinfo");
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser userId = mAuth.getCurrentUser();
            nDatabase.child(userId.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    // Hier worden de benodigde variabelen benoemd
                    User information = dataSnapshot.getValue(User.class);
                    Boolean old = true;
                    TextView No= view.findViewById(R.id.No);
                    String num = No.getText().toString();
                    final Long number = Long.valueOf(num);
                    ArrayList<pokemon> favorites = new ArrayList<pokemon>();

                    // Kijk of de favorieten leeg is
                    if (information.Favorites!=null){
                        favorites = information.Favorites;

                    // Als de aangeklikte item in favorieten zit dan verwijdert hij deze
                    for (int s = 0; s < favorites.size(); s++) {
                        if (favorites.get(s).no==number) {
                            favorites.remove(s);
                            dataSnapshot.getRef().child("Favorites").setValue(favorites);
                            LinearLayout row = view.findViewById(R.id.Complete);
                            row.setBackgroundColor(0);
                            old = false;
                            CharSequence text = "Removed from your Favorites";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), text, duration);
                            toast.show();
                        }}}

                    /* Als de pokemon nieuw is,
                     voegt hij deze toe aan de favorieten en maakt hem geel */
                    if (old) {
                        LinearLayout row = view.findViewById(R.id.Complete);
                        row.setBackgroundColor(getContext().getColor(R.color.colorAccent));
                        // Hier haalt hij de
                        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                        DatabaseReference nDatabase1 = database1.getReference("Pokemon");

                        final ArrayList<pokemon> finalFavorites = favorites;
                        nDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                for (DataSnapshot noteDataSnapshot : dataSnapshot1.getChildren()) {
                                    pokemon Pokemons2 = noteDataSnapshot.getValue(pokemon.class);
                                    if (Pokemons2.no == number) {
                                        finalFavorites.add(Pokemons2);
                                        dataSnapshot.getRef().child("Favorites").setValue(finalFavorites);
                                        CharSequence text = "Added to your Favorites";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(getContext(), text, duration);
                                        toast.show();
                                    }}}

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }});}}

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});
            return true;
        }}
    // Opent de pokemon info dialog
    public void openDialog(String no) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PokemonInfo fragment3 = new PokemonInfo().newInstance(no);
        fragment3.show(ft, "dialog");
    }
    // Checkt of de user is ingelogd
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
    // Dit kijkt of er internet is
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


