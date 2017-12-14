package com.example.vincent.pokebattler;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Dit bestand is een java class voor de data van Users, opgehaald uit FireBase
 */

public class User {
    // Dit zijn de variabele die in deze class zitten
    public String Name;
    public String Age;
    public ArrayList<pokemon> Favorites;
    public String Gen;
    public float HighScore;

    public User( String Name, String Age, ArrayList<pokemon> Favorites, String Gen){
            this.Name = Name;
            this.Age = Age;
            this.Favorites = Favorites;
            this.Gen = Gen;
            this.HighScore = 0;
    }
    public User(){};

    // Hiermee wordt informatie opgeslagen van nieuwe gebruikers
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name",Name);
        result.put("Age",Age);
        result.put("Favorites",Favorites);
        result.put("Gen",Gen);
        result.put("HighScore",HighScore);
        return result;
    }

}
