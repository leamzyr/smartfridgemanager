package app.tse.recipe_search;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.tse.SwitchMenu;
import app.tse.Unit;
import app.tse.models.IngredientModel;
import app.tse.models.Instruction;
import app.tse.models.Recipe;

public class Search {
	
	private static int totalResults = 0;
	private static String apiKey = "62d47ebdada042ae9d374b63fecbff1d"; //this line will be replaced when config file is implemented
	/*
	 * this method takes a String of comma separated ingredients as input
	 * it outputs a list of Recipe objects
	 */
	public static List<Recipe> RecipeSearch(SwitchMenu menu, int numberResults){
		StringBuilder parsedIngredients = parseIngredientList(menu.listIngredient);
		StringBuilder parsedAllergens = parseAllergensList(menu.getListAllergens());
		StringBuilder apiUrl = new StringBuilder("https://api.spoonacular.com/recipes/complexSearch?apiKey="+ apiKey + parsedIngredients + parsedAllergens +"&number=" + numberResults + "&ignorePantry=true&sort=max-used-ingredients&fillIngredients=true&addRecipeInformation=true");
		//StringBuilder apiUrl = new StringBuilder("https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + parsedIngredients + parsedAllergens + "&addRecipeInformation=true&fillIngredients=true");
		
		//creating http client for http requests
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiUrl.toString()))
				.build();
		System.out.println(request);
		//output list of recipes
		ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
		
		try {
			//sending request and retrieving result
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			ObjectMapper objectMapper = new ObjectMapper();
			
			//Get number of total results
			JsonNode jsonNode = objectMapper.readTree(response.body());
			totalResults = jsonNode.get("totalResults").asInt();
			
			//Get recipes informations
			jsonNode = jsonNode.get("results");
			//parsing Json result to put recipes into list
			for(JsonNode recipe : jsonNode) { //New Recipe
				
				// Reset ingredients and instructions list
				ArrayList<IngredientModel> listIngredients = new ArrayList<IngredientModel>();
				ArrayList<Instruction> listInstructions = new ArrayList<Instruction>();
				
				int id = recipe.get("id").asInt();
				String title = recipe.get("title").asText();
				String image = recipe.get("image").asText();
				int missedIngredients = recipe.get("missedIngredientCount").asInt();
				String summary = recipe.get("summary").asText();
				
				for(JsonNode ingredients : recipe.get("extendedIngredients")) {
					int ingredientId = ingredients.get("id").asInt();
					String ingredientName = ingredients.get("nameClean").asText();
					String ingredientImage = ingredients.get("image").asText();
					int ingredientQuantity = ingredients.get("amount").asInt();
					Unit unit = Unit.piece;
					try {
						 unit = Unit.valueOf(ingredients.get("measures").get("metric").get("unitShort").asText().toLowerCase());
					} catch (IllegalArgumentException ex) {
					}
					
					Date ingredientExpirationDate = new Date();
					IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, ingredientQuantity, unit, ingredientExpirationDate);
					listIngredients.add(ingredient);
				}

				//Collect missed ingredients in listIngredient
				for(JsonNode ingredients : recipe.get("missedIngredients")) {

					int ingredientId = ingredients.get("id").asInt();
					String ingredientName = ingredients.get("originalName").asText();
					String ingredientImage = ingredients.get("image").asText();
					int ingredientQuantity = ingredients.get("amount").asInt();
					Unit unit = Unit.piece;
					try {
						 unit = Unit.valueOf(ingredients.get("unitShort").asText().toLowerCase());
					} catch (IllegalArgumentException ex) {
					}
					Date ingredientExpirationDate = new Date(); // Search in JSON if there is any expiration Date : TO CHANGE IF REALLY NEEDED
					IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, ingredientQuantity, unit, ingredientExpirationDate);
					listIngredients.add(ingredient);
				}
				//Collect used ingredients in listIngredient
				for(JsonNode ingredients : recipe.get("usedIngredients")) {

					int ingredientId = ingredients.get("id").asInt();
					String ingredientName = ingredients.get("originalName").asText();
					String ingredientImage = ingredients.get("image").asText();
					int ingredientQuantity = ingredients.get("amount").asInt();
					Unit unit = Unit.piece;
					try {
						 unit = Unit.valueOf(ingredients.get("unitShort").asText().toLowerCase());
					} catch (IllegalArgumentException ex) {
					}
					Date ingredientExpirationDate = new Date(); // Search in JSON if there is any expiration Date : TO CHANGE IF REALLY NEEDED
					IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, ingredientQuantity, unit, ingredientExpirationDate);
					listIngredients.add(ingredient);
				}
				
				for(JsonNode analyzedInstructions : recipe.get("analyzedInstructions")) {
					for(JsonNode instructions : analyzedInstructions.get("steps")) {
						int number = instructions.get("number").asInt();
						String step = instructions.get("step").asText();
						ArrayList<IngredientModel> stepIngredients = new ArrayList<IngredientModel>();
						for(JsonNode ingredients : instructions.get("ingredients")) {
							int ingredientId = ingredients.get("id").asInt();
							String ingredientName = ingredients.get("name").asText();
							String ingredientImage = ingredients.get("image").asText();
							Date ingredientExpirationDate = new Date();
							IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, 0, Unit.piece, ingredientExpirationDate);
							stepIngredients.add(ingredient);
						}
						Instruction instruction = new Instruction(number, step, stepIngredients);
						listInstructions.add(instruction);
					}
				}
					
				Recipe newRecipe = new Recipe(id, title, image, listIngredients.size(), missedIngredients, listIngredients, "\u2606", listInstructions, summary);
				recipeList.add(newRecipe);
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		return recipeList;
	}
	
	public static StringBuilder parseIngredientList(List<IngredientModel> listIngredient){
		StringBuilder parsedIngredients = new StringBuilder("&includeIngredients=");

		for (IngredientModel ingredient: listIngredient) {
			parsedIngredients.append(ingredient.getName() + ",");
		}
        
		for (int i = 0; i < parsedIngredients.length(); i++) {
            if (parsedIngredients.charAt(i) == ' ') {
            	parsedIngredients.replace(i, i + 1, "%20");
            }
        }
		if (parsedIngredients.length() > 0) {
	        parsedIngredients.deleteCharAt(parsedIngredients.length() - 1);
	    }
		return parsedIngredients;
	}
	
	// Add allergens to the query
	public static StringBuilder parseAllergensList(List<String> listAllergens){
		StringBuilder parsedAllergens = new StringBuilder("&intolerances=");
	    for (String allergen : listAllergens) {
	        parsedAllergens.append(allergen + ",");
	    }
	    
	    for (int i = 0; i < parsedAllergens.length(); i++) {
            if (parsedAllergens.charAt(i) == ' ') {
            	parsedAllergens.replace(i, i + 1, "%20");
            }
        }
	    if (parsedAllergens.length() > 0) {
	        parsedAllergens.deleteCharAt(parsedAllergens.length() - 1);
	    }
	    return parsedAllergens;
    }	
	
	public static List<Recipe> RecipeRetrieve(SwitchMenu menu, List<String> listFavorite){
		List<Recipe> listRecipes = new ArrayList<Recipe>();
		List<String> urlRequests = parseFavoriteList(listFavorite);
		for (String url : urlRequests) {
			//creating http client for http requests
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url.toString()))
					.build();			
			try {
				//sending request and retrieving result
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				ObjectMapper objectMapper = new ObjectMapper();
				
				JsonNode jsonNode = objectMapper.readTree(response.body()).get("results");
				
				for(JsonNode recipe : jsonNode) { //New Recipe
					
					// Reset ingredients and instructions list
					ArrayList<IngredientModel> listIngredients = new ArrayList<IngredientModel>();
					ArrayList<Instruction> listInstructions = new ArrayList<Instruction>();
					
					int id = recipe.get("id").asInt();
					String title = recipe.get("title").asText();
					String image = recipe.get("image").asText();
					int missedIngredients = recipe.get("missedIngredientCount").asInt();
					String summary = recipe.get("summary").asText();
					
					for(JsonNode ingredients : recipe.get("extendedIngredients")) {
						int ingredientId = ingredients.get("id").asInt();
						String ingredientName = ingredients.get("nameClean").asText();
						String ingredientImage = ingredients.get("image").asText();
						int ingredientQuantity = ingredients.get("amount").asInt();
						Unit unit = Unit.piece;
						try {
							 unit = Unit.valueOf(ingredients.get("measures").get("metric").get("unitShort").asText().toLowerCase());
						} catch (IllegalArgumentException ex) {
						}
						
						Date ingredientExpirationDate = new Date();
						IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, ingredientQuantity, unit, ingredientExpirationDate);
						listIngredients.add(ingredient);
					}
					
					//Collect missed ingredients in listIngredient
					for(JsonNode ingredients : recipe.get("missedIngredients")) {

						int ingredientId = ingredients.get("id").asInt();
						String ingredientName = ingredients.get("originalName").asText();
						String ingredientImage = ingredients.get("image").asText();
						int ingredientQuantity = ingredients.get("amount").asInt();
						Unit unit = Unit.piece;
						try {
							 unit = Unit.valueOf(ingredients.get("unitShort").asText().toLowerCase());
						} catch (IllegalArgumentException ex) {
						}
						Date ingredientExpirationDate = new Date(); // Search in JSON if there is any expiration Date : TO CHANGE IF REALLY NEEDED
						IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, ingredientQuantity, unit, ingredientExpirationDate);
						listIngredients.add(ingredient);
					}
					
					for(JsonNode analyzedInstructions : recipe.get("analyzedInstructions")) {
						for(JsonNode instructions : analyzedInstructions.get("steps")) {
							int number = instructions.get("number").asInt();
							String step = instructions.get("step").asText();
							ArrayList<IngredientModel> stepIngredients = new ArrayList<IngredientModel>();
							for(JsonNode ingredients : instructions.get("ingredients")) {
								int ingredientId = ingredients.get("id").asInt();
								String ingredientName = ingredients.get("name").asText();
								String ingredientImage = ingredients.get("image").asText();
								Date ingredientExpirationDate = new Date();
								IngredientModel ingredient = new IngredientModel(ingredientId, ingredientImage, ingredientName, 0, Unit.piece, ingredientExpirationDate);
								stepIngredients.add(ingredient);
							}
							Instruction instruction = new Instruction(number, step, stepIngredients);
							listInstructions.add(instruction);
						}
					}
						
					Recipe newRecipe = new Recipe(id, title, image, listIngredients.size(), missedIngredients, listIngredients, "\u2606", listInstructions, summary);
					listRecipes.add(newRecipe);
				}
				
			}catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listRecipes;
	}
	
	public static List<String> parseFavoriteList(List<String> listFavorite){
		List<String> urlRequests = new ArrayList<>();
		for (String recipe: listFavorite) {
			recipe = recipe.replace(" ", "%20");
			String apiUrl = new String("https://api.spoonacular.com/recipes/complexSearch?apiKey="+ apiKey + "&titleMatch=" + recipe + "&ignorePantry=true&fillIngredients=true&addRecipeInformation=true");
			System.out.println(apiUrl);
			urlRequests.add(apiUrl);
		}
		return urlRequests;
	}
	
	// Getter
		public static int getTotalResults() {
			return totalResults;
		}
}
