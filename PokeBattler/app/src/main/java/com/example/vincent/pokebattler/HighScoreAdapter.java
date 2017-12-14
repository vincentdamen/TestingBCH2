package com.example.vincent.pokebattler;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/* In dit bestand wordt de ListAdapter gemaakt voor de Highscore */

class HighScoreAdapter extends ArrayAdapter<User> {
    // Hier worden de variabelen benoemd
    private Context context;
    private ArrayList<User> Users;

    // Hiermee kan een nieuwe adapter aangemaakt worden
    public HighScoreAdapter(Context context,int resource, ArrayList<User> Users) {
            super(context,resource, Users);
            this.context = context;
            this.Users = Users;
            }

    // getView maakt en update de listview
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Hier wordt de URL voor het plaatje voorbereid
        String Location = "sprites/0" + Users.get(position).Favorites.get(0).no + ".png";

        // Hier wordt de storage van firebase aangeroepen om het plaatje op te halen
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(Location);

        // Hier wordt de layout vastgesteld
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.row_layout_highscore,   null);

        // Hier worden de benodigde variabelen aangeroepen
        TextView Name = (TextView) view.findViewById(R.id.UserName);
        final TextView Number = (TextView) view.findViewById(R.id.Place);
        TextView Score = (TextView) view.findViewById(R.id.Score);
        final ImageView imageView = (ImageView) view.findViewById(R.id.FavoritePokemon);

        // Hier wordt de storage geopend en het plaatje opgehaald
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                // Hier wordt het plaatje geplaatst in de placeholder
                Glide.with(context).load(imageURL).into(imageView);
                }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
        public void onFailure(@NonNull Exception exception) {}});

        // Hier wordt de tekst in de row gezet
        Name.setText(Users.get(position).Name);
        Number.setText(position +1 + "");
        Score.setText(String.format("%.2f", Users.get(position).HighScore)+"");

        // Hier wordt firebase aangeroepen om de Highscore van de user te markeren
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nDatabase = database.getReference("Userinfo");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userId = mAuth.getCurrentUser();
        nDatabase.child(userId.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Hier worden de gegevens van de User opgehaald
                        User information = dataSnapshot.getValue(User.class);
                        if(information.HighScore==Users.get(position).HighScore && Objects.equals(Users.get(position).Name, information.Name)){

                            // Hier wordt de achtergrond kleur aangepast
                            LinearLayout row = view.findViewById(R.id.Complete);
                            row.setBackgroundColor(getContext().getColor(R.color.colorAccent));
                        }}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                }
            );
        return view;
    }
}