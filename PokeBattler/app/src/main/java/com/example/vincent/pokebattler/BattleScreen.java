package com.example.vincent.pokebattler;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Objects;
import java.util.Random;

/**
 * Dit bestand verzorgt het game-element in de app
 */
public class BattleScreen extends Fragment {
    // Hier worden de benodigde variabelen van te voren benoemd

    // Chosenbattles bevat later alle ingeladen fights uit firebase
    public ArrayList<battles> chosenBattles = new ArrayList<battles>();
    // Dit zijn de pokemonNo die we tijdelijk opslaan om later na te kunnen kijken
    public String numberA = "34";
    public String numberB = "3";
    // AWins bevat een boolean die de winnaar van het gevecht aanwijst
    public boolean AWins = true;
    // Dit zijn statistieken die uitmaken voor oa de puntentelling
    public int correctAnswers = 0;
    public int totalFights = 0;
    public float Score = 0;
    public int Streak = 0;
    public int Question = 0;
    public float Reward = 0;

    // onCreateView maakt de fragment aan en bereidt het spel voor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view= inflater.inflate(R.layout.fragment_battle_screen, container, false);

        checkinternet();
        // Hieronder worden de objecten in de view benoemd, om aan te kunnen passen
        TextView start = view.findViewById(R.id.StartGame);
        FloatingActionButton A = view.findViewById(R.id.infoA);
        FloatingActionButton B = view.findViewById(R.id.InfoB);
        ImageView imageA = view.findViewById(R.id.ImageA);
        ImageView imageB = view.findViewById(R.id.ImageB);

        // Hier zetten we de plaatjes op Clickable:false om zo valsspelen te voorkomen
        imageA.setClickable(false);
        imageB.setClickable(false);

        /* Hier benoemen we de OnClickListener die nodig zijn om oa het spel te starten
        * of een keuze te maken
        */
        start.setOnClickListener(new startGame());
        B.setOnClickListener(new ShowDetails());
        A.setOnClickListener(new ShowDetails());

        //Hier roepen we Firebase aan om een random selectie van de gevechten op te halen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase = database.getReference("Battles");
        nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long lengthDataset = dataSnapshot.getChildrenCount();
                int total = (int) lengthDataset;
                // hier nemen we 250 random gekozen gevechten uit de dataset
                while (chosenBattles.size()<250){
                    battles chosenBattle = dataSnapshot.child(getRandomInt(total))
                            .getValue(battles.class);
                    chosenBattles.add(chosenBattle);
                }
                // Hier zetten we de Bottom navigation weer aan
                final BottomNavigationView navigation =
                        (BottomNavigationView) getActivity().findViewById(R.id.navigation);
                navigation.setVisibility(View.VISIBLE);

                // Hierin zetten we het laadscherm uit en de elementen van het spel aan
                LinearLayout everything = getView().findViewById(R.id.Everything);
                ConstraintLayout loading = getView().findViewById(R.id.LoadingScreen);
                loading.setVisibility(View.GONE);
                everything.setVisibility(View.VISIBLE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hierin kunnen errors opgevangen worden
            }});
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Hier zetten we de Bottom navigation tijdelijk uit om te voorkomen dat de gebruiker
        *  het laden onderbreekt */
        final BottomNavigationView navigation =
                (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.INVISIBLE);
        }

    // Hiermee halen we een random getal tussen 0 en total op
    public static String getRandomInt(int total) {
        int chosen = new Random().nextInt(total+1);
        return chosen+"";}

    // Dit is een onclick class om het spel te starten
    private class startGame implements View.OnClickListener{
        @Override
        public void onClick(final View view) {
            /* eerst worden de gegevens gereset, dan wordt het eerste gevecht geladen
            Tot slot wordt de timer gestart
             */
            reset();
            setFight();
            Timerfunction();
        }}

    // Dit is een onclick class om informatie over een pokemon te zien
    private class ShowDetails implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            /* hier wordt eerst gekeken welke pokmemon opgevraagd wordt.
            Daarna wordt de juist dialog aangeroepen met openDialog
             */
            switch (view.getId()){
                case(R.id.infoA):
                    openDialog(numberA);
                    break;
                case (R.id.InfoB):
                    openDialog(numberB);
                    break;}}}

    // Deze functie regelt de timer en allerlei regels van objecten
    public void Timerfunction(){
        // Hier benoemen we de benodigde variabelen
        final ImageView imageA = getView().findViewById(R.id.ImageA);
        final ImageView imageB = getView().findViewById(R.id.ImageB);
        final TextView start = getView().findViewById(R.id.StartGame);
        final ProgressBar bar = getView().findViewById(R.id.Timer);
        final BottomNavigationView navigation =
                (BottomNavigationView) getActivity().findViewById(R.id.navigation);

        /* Wederom zal de Bottom navigation verdwijnen bij het spelen.
           Zo kan je niet weg in een spel.*/
        navigation.setVisibility(View.INVISIBLE);

        // Hier worden objecten clickable of unclickable gemaakt
        start.setClickable(false);
        imageA.setClickable(true);
        imageB.setClickable(true);

        // Hier worden onClickListeners aan de plaatjes toegewezen
        imageA.setOnClickListener(new chooseWinner());
        imageB.setOnClickListener(new chooseWinner());

        // Hier wordt de tekst van de start button verandert
        start.setText(R.string.WhosGonnaWin);

        // Hier wordt de timer benoemd en geactiveerd
        CountDownTimer timer = new CountDownTimer(120000, 100) {
            public void onTick(long millisUntilFinished) {
                bar.setProgress(120000-(int)millisUntilFinished);
            }

            public void onFinish() {
                // Als de timer afgelopen is, verandert hij de variabelen weer in oude staat
                start.setText(R.string.Rematch);
                start.setClickable(true);
                imageA.setClickable(false);
                imageB.setClickable(false);
                navigation.setVisibility(View.VISIBLE);
                // Hier wordt de score met de highscore bekeken en eventueel geupdate
                saveScore();
            }
        }.start();
    }

    // Hiermee wordt de informatie dialogs van een pokemon opgeroepen
    public void openDialog(String no) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PokemonInfo fragment3 = new PokemonInfo().newInstance(no);
        fragment3.show(ft, "dialog");
    }

    // Dit is een OnClickListener voor de plaatjes
    private class chooseWinner implements View.OnClickListener{
        // Hier worden de benodigde variabelen aangeroepen
        final ImageView imageA = getView().findViewById(R.id.ImageA);
        final ImageView imageB = getView().findViewById(R.id.ImageB);

        @Override
        public void onClick(View view) {
            /* Hier wordt gekeken op welk plaatje is geklikt en vervolgens gekeken
            of het antwoord goed is. Daarna wordt een nieuw gevecht opgevraagd. */
            switch (view.getId()){
                case(R.id.ImageA):
                    if(AWins){
                        Score += Reward;
                        if(Streak>2){
                        Score += Reward;}
                        Streak += 1;
                        correctAnswers += 1;
                        totalFights += 1;

                        imageA.setClickable(false);
                        imageB.setClickable(false);
                        setFight();
                    }
                    else{
                        Reward = 0;
                        Streak = 0;
                        totalFights +=1;

                        imageA.setClickable(false);
                        imageB.setClickable(false);
                        setFight();
                    }
                    break;

                case (R.id.ImageB):
                    if(!AWins){
                        Score += Reward;
                        if(Streak>2){
                            Score += Reward;}
                        Streak += 1;
                        correctAnswers += 1;
                        totalFights += 1;

                        imageA.setClickable(false);
                        imageB.setClickable(false);
                        setFight();
                    }
                    else{
                        Reward = 0;
                        Streak = 0;
                        totalFights +=1;

                        imageA.setClickable(false);
                        imageB.setClickable(false);
                        setFight();
                    }
                    break;
            }
        }
    }

    // Hier wordt het nieuwe gevecht aangeroepen en de benodigde spullen ingeladen
    public void setFight(){
        checkinternet();
        // Hier worden de Benodigde variabelen opgehaald
        final ImageView imageA = getView().findViewById(R.id.ImageA);
        final ImageView imageB = getView().findViewById(R.id.ImageB);
        // Hier wordt het nieuwe gevecht opgehaald
        final battles nextBattle = chosenBattles.get(Question);
        Question+=1;

        // Hier wordt de te verdienen punten berekent
        Reward = (float) Math.pow(2,nextBattle.Score);

        // Hier wordt de winnaar van het nieuwe gevecht opgeslagen
        AWins = nextBattle.Winner == nextBattle.First_pokemon;

        // Hier wordt firebase aangeroepen om de informatie over de pokemon op te halen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase= database.getReference("Pokemon");
        nDatabase.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Voor elke pokemon wordt gekeken of ze matchen aan de eerst en tweede pokemon
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    pokemon Pokemons2 = noteDataSnapshot.getValue(pokemon.class);
                    if (Pokemons2.DexNo == nextBattle.First_pokemon) {
                        updateInfo(Pokemons2,true);
                    }
                    else if(Pokemons2.DexNo == nextBattle.Second_pokemon){
                        updateInfo(Pokemons2,false);

                    }
                }
                // Hier worden plaatjes weer clickable gemaakt

                imageA.setClickable(true);
                imageB.setClickable(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier worden errors afgevangen
            }
        });
    }

    // Deze methode plaatst de plaatjes in de container
    public void PlacePicture(final ImageView placeholder, String location){
        // Hier wordt het plaatje opgehaald
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                .child(location.toLowerCase());
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                // Hier wordt het plaatje in de view gezet
                Glide.with(getContext()).load(imageURL).into(placeholder);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    // Deze functie schrijft de opgehaalde gegevens naar de juiste containers
    public void updateInfo(pokemon input,Boolean A){
        // Hier worden de URL's naar de plaatjes voorbereid
        String PokePhoto = "sprites/0"+input.no+".png";
        String PokeType1 = "types/"+input.Type1+".png";
        String PokeType2 = "types/"+input.Type2+".png";

        // Als het over container A gaat zal A true zijn
        if(A){
            numberA=""+input.no;

            // Hier worden de variabelen aan geroepen
            ImageView Type1 = getView().findViewById(R.id.Type1A);
            ImageView Type2 = getView().findViewById(R.id.Type2A);
            ImageView Picture = getView().findViewById(R.id.ImageA);
            TextView name = getView().findViewById(R.id.NameA);

            // Hier zetten we de Types leeg zodat het er netter uitziet tijdens het laden
            Type1.setImageResource(0);
            Type2.setImageResource(0);

            // Hier plaatsen we de plaatjes en gegevens
            PlacePicture(Picture,PokePhoto);
            name.setText(input.Name);

            // Hier kijken we hoeveel types de pokemon is
            if(Objects.equals("None", input.Type2)){
                // Als de pokemon 1 type heeft, verdwijnt type 2 zodat type 1 in het midden komt
                Type2.setVisibility(View.GONE);
                PlacePicture(Type1,PokeType1);
            }
            else {
                // Als er 2 types zijn plaats hij deze en maakt Type 2 voor de zekerheid visible
                PlacePicture(Type1, PokeType1);
                PlacePicture(Type2, PokeType2);
                Type2.setVisibility(View.VISIBLE);
            }
        }
        else{
            numberB=""+input.no;

            // Hier worden de variabelen aan geroepen
            ImageView Type1 = getView().findViewById(R.id.Type1B);
            ImageView Type2 = getView().findViewById(R.id.Type2B);
            ImageView Picture = getView().findViewById(R.id.ImageB);
            TextView name = getView().findViewById(R.id.NameB);

            // Hier zetten we de Types leeg zodat het er netter uitziet tijdens het laden
            Type1.setImageResource(0);
            Type2.setImageResource(0);

            // Hier plaatsen we de plaatjes en gegevens
            PlacePicture(Picture,PokePhoto);
            name.setText(input.Name);

            // Hier kijken we hoeveel types de pokemon is
            if(Objects.equals("None", input.Type2)){
                // Als de pokemon 1 type heeft, verdwijnt type 2 zodat type 1 in het midden komt
                Type2.setVisibility(View.GONE);
                PlacePicture(Type1,PokeType1);

            }
            else{
                // Als er 2 types zijn plaats hij deze en maakt Type 2 voor de zekerheid visible
                Type2.setVisibility(View.VISIBLE);
                PlacePicture(Type1,PokeType1);
                PlacePicture(Type2,PokeType2);
            }
        }

        // Hier wordt de resterende gegevens geplaatst
        TextView answered = getView().findViewById(R.id.answered);
        TextView score = getView().findViewById(R.id.score);
        TextView correct = getView().findViewById(R.id.correct);
        TextView streak = getView().findViewById(R.id.streak);
        answered.setText(getString(R.string.TotalBattle)+totalFights);
        score.setText(String.format("%.2f", Score)+getString(R.string._point));
        correct.setText(getString(R.string.Correct_)+correctAnswers);

        // Hier kijken we of de verdubbelaar geactiveerd moet worden
        if(Streak>2){
            streak.setVisibility(View.VISIBLE);
        }
        else{
            streak.setVisibility(View.INVISIBLE);
        }
    }

    // Hier wordt de score vergeleken met de highScore en eventueel opgeslagen
    public void saveScore(){
        // Hier maken we verbinding met firebase om de highscore te checken
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userId = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase= database.getReference("Userinfo");

        //We halen de gebruiker op aan de hand van de Uid
        nDatabase.child(userId.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        float oldScore = Float.parseFloat(dataSnapshot.child("HighScore")
                                .getValue().toString());

                        // Hier wordt de score vergeleken
                        if (oldScore < Score) {

                            /* Als het een highScore is wordt deze opgeslagen en
                            krijgt de gebruiker een toast te zien */
                            dataSnapshot.getRef().child("HighScore").setValue(Score);
                            CharSequence text = getString(R.string.newHighScore);
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), text, duration);
                            toast.show();
                        }}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Hier worden errors opgevangen
                    }
                }
        );
    }

    // Deze functie reset de statistieken om een nieuwe ronden te kunnen spelen
    public void reset(){
        Reward=0;
        Score=0;
        correctAnswers=0;
        Streak=0;
        totalFights=0;
    }

    // Dit zorgt ervoor dat de gebruikter ingelogd blijft en anders zich aan moet melden
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
