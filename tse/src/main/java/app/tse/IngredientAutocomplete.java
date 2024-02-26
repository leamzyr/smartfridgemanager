package app.tse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IngredientAutocomplete {
	    
	public static String Autocomplete(String selection) {
		String choice = "Error";
        String apiKey = "6b134f2e6d85484386b87b37aec72fcd";
        String apiUrl = "https://api.spoonacular.com/food/ingredients/autocomplete?apiKey=" + apiKey + "&query=" + selection;
        apiUrl = apiUrl.replace(" ", "%20");


        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<String> namesList = new ArrayList<String>();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body());

                for (JsonNode element : jsonNode) {
                    String name = element.get("name").asText();
                    namesList.add(name);
                }
                
                selection = selection.replaceAll("\\s+", " ");
                selection = selection.trim();
                if(namesList.contains(selection)) {
                	return selection;
                }
                if(namesList.size()==1) {
                	return namesList.get(0);
                }
                
                choice = createOptionPaneWithButtons(namesList);

            } catch (IOException e) {
                e.printStackTrace();
                return choice;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return choice;
        }
        return choice;
	}
	
    public static String createOptionPaneWithButtons(List<String> buttonNames) {
    	if(buttonNames.size()==0) {
    		return "Error";
    	}
        final String[] result = {null}; // Tableau pour stocker le résultat

        // Création d'un tableau d'objets pour les boutons
        Object[] options = buttonNames.toArray();

        // Création du JOptionPane avec les boutons
        int choice = JOptionPane.showOptionDialog(null,"Sélectionnez une option:","Options",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);

        // Vérification du choix de l'utilisateur
        if (choice >= 0 && choice < buttonNames.size()) {
            result[0] = buttonNames.get(choice);
        }

        return result[0];
    }
}