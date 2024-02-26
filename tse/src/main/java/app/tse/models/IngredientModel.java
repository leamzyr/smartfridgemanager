package app.tse.models;

import java.util.Date;
import java.util.List;

import app.tse.Unit;

public class IngredientModel implements Comparable<IngredientModel>{
	private int id;
	private String image;
	private String name;
	private int quantity;
	private Unit unit = Unit.piece;
	private Date expirationDate;
	
	//Constructor
	public IngredientModel(int id, String image, String name, int quantity, Unit unit, Date expirationDate) {
		this.id = id;
		this.image = image;
		this.name = name;
		this.quantity = quantity;
		this.expirationDate = expirationDate;
		this.unit = unit;
	}
	
	//Getters
	public int getId() {
		return this.id;
	}
	public String getImage() {
		return this.image;
	}
	public String getName() {
		return this.name;
	}
	public int getQuantity() {
		return this.quantity;
	}
	public Date getExpirationDate() {
		return this.expirationDate;
	}
	public Unit getUnit() {
		return this.unit;
	}
	//Setters
	public void setQuantity(int newQuantity) {
		this.quantity = newQuantity;
	}
	public void setExpirationDate(Date newExpirationDate) {
		expirationDate = newExpirationDate;
	}
	public void setUnit(Unit newUnit) {
		unit = newUnit;
	}

	@Override
	public int compareTo(IngredientModel o) {
		Date d1 = this.expirationDate;
		Date d2 = o.getExpirationDate();
		return d1.compareTo(d2);
	}
	public boolean isInside(List<IngredientModel> listIngredient) {
		for(IngredientModel ingredient : listIngredient){
			if(this.getName().equals(ingredient.getName())){return true;}
		}
		return false;
	}

}
