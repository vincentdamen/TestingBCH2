package com.example.vincent.pokebattler;

import android.annotation.SuppressLint;
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

/* In dit bestand wordt de ListAdapter gemaakt voor de PokeDex */

class dexAdapter extends ArrayAdapter<pokemon>{
    // Hier worden de variabelen benoemd
    private Context context;
    private ArrayList<pokemon> pokemons;
    private StorageReference mStorageRef;

    // Hiermee kan een nieuwe adapter aangemaakt worden
    public dexAdapter(Context context,int resource, ArrayList<pokemon> pokemons) {
        super(context,resource, pokemons);
        this.context = context;
        this.pokemons = pokemons;
    }

    // getView maakt en update de listview
    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    // Hier wordt de URL voor het plaatje voorbereid
    String Location = "sprites/0" + pokemons.get(position).no + ".png";

    // Hier wordt de storage van firebase aangeroepen om het plaatje op te halen
    final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(Location);

    // Hier wordt de layout vastgesteld
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(
            Activity.LAYOUT_INFLATER_SERVICE);
    final View view = inflater.inflate(R.layout.row_layout, null);

    // Hier worden de benodigde variabelen aangeroepen
    final ImageView imageView = (ImageView) view.findViewById(R.id.PokePicture);
    TextView Name = (TextView) view.findViewById(R.id.PokeName);
    TextView Number = (TextView) view.findViewById(R.id.No);

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
        public void onFailure(@NonNull Exception exception) {
            // Handle any errors
        }
    });

    // Hier wordt de tekst in de row gezet
    long no = pokemons.get(position).no;
    Name.setText(pokemons.get(position).Name);
    Number.setText(no + "");

    // Hier wordt firebase aangeroepen om de favorieten te markeren van de User
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference nDatabase = database.getReference("Userinfo");
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userId = mAuth.getCurrentUser();
    nDatabase.child(userId.getUid()).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){

                    // Hier worden de gegevens van de User opgehaald
                    User information = dataSnapshot.getValue(User.class);
                    // Hier wordt gecheckt of de favorieten niet leeg zijn
                    if(information.Favorites!=null){
                        for (int s=0;s<information.Favorites.size();s++){
                            // Hier wordt de achtergrond kleur aangepast
                            if(information.Favorites.get(s).no==pokemons.get(position).no){
                                LinearLayout row = view.findViewById(R.id.Complete);
                                row.setBackgroundColor(getContext().getColor(R.color.colorAccent));
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}

            });
    return view;
}}

