package app.tse.models;

import java.util.ArrayList;

public class Instruction {
	private int number;
	private String step;
	private ArrayList<IngredientModel> ingredients;
	
	//Constructor
	public Instruction(int number, String step, ArrayList<IngredientModel> ingredients) {
		this.number = number;
		this.step = step;
		this.ingredients = ingredients;
	}
	
	//Getters
	public int getNumber() {
		return this.number;
	}
	public String getStep() {
		return this.step;
	}
	public ArrayList<IngredientModel> getIngredients() {
		return this.ingredients;
	}
}
