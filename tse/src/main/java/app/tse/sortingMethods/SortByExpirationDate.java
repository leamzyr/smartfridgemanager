package app.tse.sortingMethods;

import java.util.ArrayList;
import java.util.List;

import app.tse.SwitchMenu;
import app.tse.models.IngredientModel;
import app.tse.models.Recipe;

public class SortByExpirationDate {
	public List<Recipe> Sort(SwitchMenu menu, int number) {
	    List<IngredientModel> listIngredientSorted = new ArrayList<>();
	    //We sort ingredients first
	    while (menu.listIngredient.size() > 1) {
	        IngredientModel closestExpirationDate = menu.listIngredient.get(0);
	        int index = 0;
	        for (int i = 1; i < menu.listIngredient.size(); i++) {
	            if (closestExpirationDate.compareTo(menu.listIngredient.get(i)) == 1) {
	                closestExpirationDate = menu.listIngredient.get(i);
	                index = i;
	            }
	        }
	        listIngredientSorted.add(closestExpirationDate);
	        menu.listIngredient.remove(index);
	    }

	    listIngredientSorted.add(menu.listIngredient.get(0));
	    menu.listIngredient = listIngredientSorted;
	    
	    List<Recipe> listRecipeSorted = new ArrayList<>();
	    //Then we sort recipe
    	int listSize = menu.getListRecipe().size() - 1;
    	int index = 0;
    	for (IngredientModel ingredient : listIngredientSorted) {
    		boolean flag = false;
    		for(int i = listSize - index; i >= 0; i-- ) {
    			Recipe recipe = menu.getListRecipe().get(i);
    			for (IngredientModel recipeIngredient : recipe.getIngredientList()) {
	                if (ingredient.getName().equals(recipeIngredient.getName())) {
	                    flag = true;
	                    break;
	                }
	            }
    			//If it contains the ingredient, remove the recipe and increment index
    			if(flag) {
    				listRecipeSorted.add(recipe);
    				menu.getListRecipe().remove(i);
    				index++;
    				flag = false;
    			}
	        }
    	}
	    return listRecipeSorted;
	}

}
