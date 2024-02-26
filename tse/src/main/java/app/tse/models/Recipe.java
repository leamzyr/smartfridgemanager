package app.tse.models;

import java.util.ArrayList;

public class Recipe {
	private int id;
	private String title;
	private String image;
	private int nbIngredients;
	private int nbMissing;
	private ArrayList<IngredientModel> ingredientList;
	private ArrayList<Instruction> instructionList;
	private String summary;
	private String is_favourite="\u2606";
	// other attributes can be added later for other functionalities
	// ex: List of used and unused ingredients
	
	//Constructor
	public Recipe(int id, String title, String image, int nbIngredients, int nbMissing, ArrayList<IngredientModel> ingredientList,String favourite, ArrayList<Instruction> instructionList, String summary) {
		this.id = id;
		this.image = image;
		this.title = title;
		this.nbIngredients = nbIngredients;
		this.nbMissing = nbMissing;
		this.ingredientList = ingredientList;
		this.instructionList = instructionList;
		this.summary = summary;
		this.setIs_favourite(favourite);
	}
	
	//Getters
	public int getId() {
		return this.id;
	}
	public String getTitle() {
		return this.title;
	}
	public String getImage() {
		return this.image;
	}
	public int getNbIngredients() {
		return this.nbIngredients;
	}
	public int getNbMissing() {
		return this.nbMissing;
	}
	public ArrayList<IngredientModel> getIngredientList() {
		return this.ingredientList;
	}
	public ArrayList<Instruction> getInstructionList() {
		return this.instructionList;
	}
	public String getSummary() {
		return this.summary;
	}
	
	//Setters
	public void setsummary(String summary) {
		this.summary = summary;
	}
	
	public boolean containsIngredient(String ingredientName) {
	    for (IngredientModel recipeIngredient : ingredientList) {
	        if (ingredientName.equals(recipeIngredient.getName())) {
	            return true;
	        }
	    }
	    return false;
	}

	public String getIs_favourite() {
		return is_favourite;
	}

	public void setIs_favourite(String is_favourite) {
		this.is_favourite = is_favourite;
	}
}
