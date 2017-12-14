package com.example.vincent.pokebattler;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


/**
* Dit bestand bouwt de dialogfragment om pokemon info te laten zien
*/
public class UserInfoDialog extends DialogFragment {
    // hiermee halen we het gegevens van de speler op
    public UserInfoDialog newInstance(String Name,float HighScore) {
        UserInfoDialog f = new UserInfoDialog();
        Bundle args = new Bundle();
        args.putString("Name", Name);
        args.putFloat("score",HighScore);
        f.setArguments(args);
        return f;
    }

    // Hier creëren we de dialog
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  dit maakt het onmogelijk om tijdens het laden de dialog weg te klikken
        getDialog().setCanceledOnTouchOutside(false);

        final String Name = getArguments().getString("Name");
        final Float HighScore = getArguments().getFloat("score");

        // Hier wordt firebase opgehaald om informatie van de desbetreffende pokemon op te halen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase= database.getReference("Userinfo");
        nDatabase.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User information = noteDataSnapshot.getValue(User.class);
                    // Hier halen we de juiste gebruiker eruit
                    if (Objects.equals(information.Name, Name) &&
                            Float.parseFloat(String.format("%.2f", information.HighScore)
                                    .replace(",",".")) ==HighScore) {

                        // hier wordt de informatie in de dialog gezet
                        updateInfo(information);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info_dialog, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Hier wordt de informatie in de dialog geplaatst
    public void updateInfo(User input){
        String PokePhoto = "sprites/0"+input.Favorites.get(0).no+".png";
        TextView name = getView().findViewById(R.id.UserNameD);
        TextView Age = getView().findViewById(R.id.Age);
        TextView Gen = getView().findViewById(R.id.Gen);
        TextView High = getView().findViewById(R.id.HighScoreD);
        TextView Favorites = getView().findViewById(R.id.Favorites);
        ImageView Picture = getView().findViewById(R.id.ImageFav);
        PlacePicture(Picture,PokePhoto);
        name.setText(input.Name);
        High.setText("Highscore: "+String.format("%.2f", input.HighScore));
        Age.setText("Age: "+input.Age);
        Gen.setText("Favorite Generation: "+input.Gen);
        Favorites.setText(GetFavorites(input.Favorites));
        getDialog().setCanceledOnTouchOutside(true);


    }
    // Hier wordt het plaatje ingeladen van de favo pokemon
    public void PlacePicture(final ImageView placeholder, String location){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(location.toLowerCase());
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getContext()).load(imageURL).into(placeholder);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    // Hier wordt een string met de favo pokemon gemaakt
    public String GetFavorites(ArrayList<pokemon> favorites){
        String Result = "Favorite pokemon: ";
        for(int i =0;i<favorites.size();i++){
            if(favorites.size()-i!=1){
                Result += favorites.get(i).Name+", ";}
            else{
                Result += favorites.get(i).Name;
            }

        }
        return Result;
    }

    // Dit checkt of de user is ingelogd
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

}
