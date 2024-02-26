package src.test.java.recipe_search;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import app.tse.Unit;
import app.tse.models.IngredientModel;
import app.tse.recipe_search.Search;

public class TestRecipeSearch {

	String parsedList;
	IngredientModel ing1;
	IngredientModel ing2;
	List<IngredientModel> listIngredient;
	Date ingredientExpirationDate;
	
	
	@Before
	public void init() {
		listIngredient = new ArrayList<IngredientModel>();
		ingredientExpirationDate = new Date();
		ing1 = new IngredientModel(0,"image","apple", 1, Unit.piece,  ingredientExpirationDate );
		ing2 = new IngredientModel(1, "image2", "apple pie", 3, Unit.piece, ingredientExpirationDate );
		listIngredient.add(ing1);
		listIngredient.add(ing2);
		parsedList = "apple,apple%20pie";
	}

	@Test
	public void testparseIngredientList() {
		StringBuilder parsedIngredientList = Search.parseIngredientList(listIngredient);
		assertTrue(parsedIngredientList.equals(parsedList));
	}
}