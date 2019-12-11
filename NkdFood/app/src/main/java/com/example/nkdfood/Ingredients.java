package com.example.nkdfood;


import java.util.ArrayList;

public class Ingredients {
    public ArrayList<String> ingredients;

    public Ingredients(){

    }

    public Ingredients(ArrayList<String> ing){
        this.ingredients = ing;
    }

    public ArrayList<String> getIngredients(){
        return this.ingredients;
    }

    public void setIngredients(ArrayList<String> ing){
        this.ingredients = ing;
    }
}
