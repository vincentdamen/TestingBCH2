package com.example.vincent.pokebattler;

/* Dit bestand is een java class voor de data van Pokemon, opgehaald uit FireBase
 */
public class pokemon   {
    // Dit zijn de variabele die in deze class zitten
    public String Name;
    public long Attack;
    public long Defense;
    public long DexNo;
    public long Generation;
    public long HP;
    public boolean Legendary;
    public long no;
    public long SpAtk;
    public long SpDef;
    public long Speed;
    public String Type1;
    public String Type2;

    public void pokemon(String Name,long Attack
                        , long Defense
                        , long DexNo
                        , long Generation
                        , long HP
                        , boolean Legendary
                        , int no
                        , long SpAtk
                        , long SpDef
                        , long Speed
                        , String Type1
                        , String Type2){
        this.Name = Name;
        this.Attack = Attack;
        this.Defense = Defense;
        this.DexNo = DexNo;
        this.Generation = Generation;
        this.HP = HP;
        this.Legendary = Legendary;
        this.no = no;
        this.SpAtk = SpAtk;
        this.SpDef = SpDef;
        this.Speed = Speed;
        this.Type1 = Type1;
        this.Type2 = Type2;
    }
}
