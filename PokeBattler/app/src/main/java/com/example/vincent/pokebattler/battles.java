package com.example.vincent.pokebattler;

/* Dit bestand is een java class voor de data van battles, opgehaald uit FireBase
 */

public class battles  {
    // Dit zijn de variabele die in deze class zitten
    public long First_pokemon;
    public long Second_pokemon;
    public long Winner;
    public float Score;

    public void pokemon(
             long First_pokemon
            ,long Second_pokemon
            ,long Winner
            ,float Score){
        this.First_pokemon = First_pokemon;
        this.Second_pokemon = Second_pokemon;
        this.Winner = Winner;
        this.Score = Score;
    }
}

