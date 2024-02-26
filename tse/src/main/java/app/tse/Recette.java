package app.tse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.tse.models.IngredientModel;
import app.tse.models.Recipe;

public class Recette {
	private static ArrayList <IngredientModel> shoppingList = new ArrayList<IngredientModel>();
	
    public static void addIngredientsToShoppingList(Recipe recipe, JPanel shoppingListField, SwitchMenu menu) {
        for (IngredientModel ingredient : recipe.getIngredientList()) {
        	for (IngredientModel ingredientFrigo : menu.listIngredient) {
        		if (!ingredientFrigo.getName().equals(ingredient.getName())&&  !shoppingList.contains(ingredient)){
        			shoppingList.add(ingredient);
        			JPanel ingredientPanel = new JPanel();
        			ingredientPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        			JLabel tiret = new JLabel("- ");
        			JTextField quantity = new JTextField(String.valueOf(ingredient.getQuantity()));
        			JLabel unit = new JLabel(String.valueOf(ingredient.getUnit()));
        			JLabel name = new JLabel(ingredient.getName());
        			ingredientPanel.add(tiret);
        			ingredientPanel.add(quantity);
        			ingredientPanel.add(unit);
        			ingredientPanel.add(name);
                    shoppingListField.add(ingredientPanel);
                    shoppingListField.setBorder(BorderFactory.createEmptyBorder(5, 30, 0, 0));
                    
                    quantity.addFocusListener(new FocusAdapter() {
            	        @Override
            	        public void focusLost(FocusEvent e) {
            	            updateUnit();
            	        }
            	        private void updateUnit() {
            	        	String newQuantityText = quantity.getText().trim();
            	        	final int index = shoppingListField.getComponentZOrder(ingredientPanel);
            	        	try {
            	        		int newQuantity = Integer.valueOf(newQuantityText);
            	        		shoppingList.get(index).setQuantity(newQuantity);
            	        	}
            	        	catch (IllegalArgumentException ex) {
            	        		quantity.setText(String.valueOf(ingredient.getQuantity()));
            	        		shoppingListField.revalidate();
            	        		shoppingListField.repaint();
            	        		JOptionPane.showMessageDialog(null, "Quantity must be an integer");
            	        	}
            	        }
            	    });
                    
                }
        	}
        }
        
		shoppingListField.revalidate();
		shoppingListField.repaint();
		
    }
     
//    private static JLabel createIngredientLabel(IngredientModel ingredient) {
//        return new JLabel("- " + ingredient.getQuantity() + " " + ingredient.getUnit() + " "  + ingredient.getName());
//    }
//    
    
	// static class method, returns a JPanel containing a picture of the dish, the name of the recipe, a detail button, and a favourite button
	public static JPanel recette(final Recipe recipe, List<IngredientModel> fridgeList, JPanel ingredientsField, List<Recipe> listFavourite, JPanel favouriteField,JPanel shoppingListField,  SwitchMenu menu, DataBase database, Integer userID){
		JButton detail = new JButton("detail");
  		JButton nutrients = new JButton("nutrients");
  		JButton addToShoppingList = new JButton("Add to Shopping List");
  		JButton favourite= new JButton(recipe.getIs_favourite());
  		final JPanel p = new JPanel();
        
        try {
            URL url = new URL(recipe.getImage());
            BufferedImage originalImage = ImageIO.read(url);
            int newWidth = 100; 
            int newHeight = 100; 
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
            p.add(imageLabel);
        } catch (IOException ex) {
            ex.printStackTrace(); 
        }
        
        p.add(new JLabel(recipe.getTitle()));	
        p.add(detail);
        p.add(nutrients);
        p.add(addToShoppingList);
        p.add(favourite);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getMinimumSize().height));

        JPanel favoriteOne = favorite(recipe, menu.listIngredient, ingredientsField, menu.getListFavourite(), favouriteField, shoppingListField, menu, MainMenu.getDataBase(), MainMenu.getSelectedUser());
        
		detail.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		detailButton(recipe, menu, ingredientsField);
        	}
        });
		
		favourite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				favouriteButton(recipe, favouriteField, favoriteOne, ingredientsField, listFavourite, favourite, database, userID, menu);
			}
		});
		
		addToShoppingList.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // Appeler la méthode pour ajouter les ingrédients à la liste de courses
		        Recette.addIngredientsToShoppingList(recipe, shoppingListField, menu);
		    }
		});
		
		nutrients.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	nutrientsButton(recipe);
		    }
		});
		
		favouriteField.addPropertyChangeListener("favourited", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if((boolean)evt.getNewValue() == false) {
					favourite.setText("\u2606");
				}
			}
		});
		
		return p;
    }
	
	public static JPanel favorite(final Recipe recipe, List<IngredientModel> fridgeList, JPanel ingredientsField, List<Recipe> listFavourite, JPanel favouriteField, JPanel shoppingListField, SwitchMenu menu, DataBase database, Integer userID){
  		JButton detail = new JButton("detail");
  		JButton nutrients = new JButton("nutrients");
  		JButton addToShoppingList = new JButton("Add to Shopping List");
  		JButton delete = new JButton("X");
  		final JPanel p = new JPanel();
        p.setLayout(new GridLayout(1,6));
        try {
            URL url = new URL(recipe.getImage());
            BufferedImage originalImage = ImageIO.read(url);
            int newWidth = 100; 
            int newHeight = 100; 
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
            p.add(imageLabel);
        } catch (IOException ex) {
            ex.printStackTrace(); 
        }
        
        p.add(new JLabel(recipe.getTitle()));	
        p.add(detail);
        p.add(nutrients);
        p.add(addToShoppingList);
        p.add(delete);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getMinimumSize().height));
        
		detail.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		detailButton(recipe, menu, ingredientsField);
        	}
        });
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	final int index = favouriteField.getComponentZOrder(p);
            	System.out.println(index);
  	          	database.connectDatabase();
  	          	System.out.println(listFavourite.get(index).getTitle());
  	          	database.deleteFavorite(listFavourite.get(index).getTitle(), MainMenu.getSelectedUser());
  	          	
  	          	database.close();
  	          	
  	            listFavourite.get(index).setIs_favourite("\u2606");
  	          	listFavourite.remove(index);
  	          	favouriteField.remove(p);
  	          	favouriteField.revalidate();
  	          	favouriteField.repaint();
  	          	favouriteField.firePropertyChange("favourited", true, false);
            }
			
		});
		
		addToShoppingList.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // Appeler la méthode pour ajouter les ingrédients à la liste de courses
		        Recette.addIngredientsToShoppingList(recipe, shoppingListField, menu);
		    }
		});
		
		nutrients.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	nutrientsButton(recipe);
		    }
		});
		return p;
    }
	
	
 	private static JScrollPane makeInfoBox(Recipe recipe, List<IngredientModel> fridgeList, JPanel ingredientsField) {

		JPanel infoBox = new JPanel();
		infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.PAGE_AXIS));
		
		ArrayList<IngredientModel> ingredientList = recipe.getIngredientList(); // getting recipe ingredient list

	    // getting and displaying recipe thumbnail image
	    try {
	        URL url = new URL(recipe.getImage());
	        Image scaledImage = ImageIO.read(url).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	        ImageIcon imageIcon = new ImageIcon(scaledImage);
	        infoBox.add(new JLabel(imageIcon));
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // getting recipe title
	    JLabel titleLabel = new JLabel(recipe.getTitle());
	    titleLabel.setHorizontalAlignment(JLabel.CENTER);
	    
	    // making button to remove recipe ingredients from fridge
	    JButton checkOff = new JButton("Remove Ingredients from Fridge");
	    checkOff.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < fridgeList.size(); i++) {
					for (int j = 0; i < ingredientList.size(); j++) {
						if(ingredientList.get(j).getName().equals(fridgeList.get(i).getName())) {
							fridgeList.remove(i);
							ingredientsField.remove(i);
			                ingredientsField.revalidate();
			                ingredientsField.repaint();
			                break;
						}
					}
				}
				
				
				
			}
		});
	    
	    // creating and displaying a JPanel containing title and button
	    JPanel headerPanel = new JPanel();
	    headerPanel.add(titleLabel);
	    headerPanel.add(checkOff);
	    infoBox.add(headerPanel);
	    
	    // displaying recipe summary
	    StyleSheet styleSheet = new StyleSheet();
	    styleSheet.addRule("body {width: 300px ; margin : auto ; text-align: justify} ");
	    JTextPane summary = new JTextPane();
	    HTMLEditorKit html = new HTMLEditorKit();
	    String list = new String("");
	    for(IngredientModel ingredient : recipe.getIngredientList()) {
	    	list = list + ingredient.getName() + ", ";
	    }
	    html.setStyleSheet(styleSheet);
	    summary.setEditorKit(html);
	    summary.setText(recipe.getSummary());
	    summary.setEditable(false);
	    infoBox.add(summary);

	    // displaying recipe's ingredient list
	    System.out.println("test into recipe ingredients section");
	    JPanel ingredientSectionPanel = new JPanel();
	    ingredientSectionPanel.setLayout(new BoxLayout(ingredientSectionPanel, BoxLayout.PAGE_AXIS));
	    JLabel ingredientSection = new JLabel("Ingredients :");
	    ingredientSection.setHorizontalAlignment(JLabel.CENTER);
	    ingredientSectionPanel.add(ingredientSection);
	    for (int i = 0; i<ingredientList.size(); i++) {
	    	System.out.println("test inside for-loop for ingredients\nindex: " + i);
	    	JPanel ingredientPanel = new JPanel();
	    	IngredientModel ingredient = ingredientList.get(i);
	    	try {
	    		// ingredient images are currently broken, how sad :(
		        URL url = new URL(ingredient.getImage());
		        Image scaledImage = ImageIO.read(url).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		        ImageIcon imageIcon = new ImageIcon(scaledImage);
		        //ingredientPanel.add(new JLabel(imageIcon));
		    } catch (MalformedURLException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	    	
	    	JLabel ingredientName = new JLabel(ingredient.getName());
	    	ingredientPanel.add(ingredientName);
	    	ingredientSectionPanel.add(ingredientPanel);
	    }
	    infoBox.add(ingredientSectionPanel);
	    System.out.println("passed infobox.add(ingredientSectionPanel, cc.xy(1 ,4));");
	    
	    JScrollPane jsp = new JScrollPane(infoBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	    return jsp;
	}

 	// this method contains the actions of the "nutrients" button
 	private static void nutrientsButton(Recipe recipe) {
 		JFrame nutrientsWindow = new JFrame("Nutrients informations");
    	String nutrientsInfo = "<html>";
    	String id = String.valueOf(recipe.getId());
    	String apiKey = "6b134f2e6d85484386b87b37aec72fcd";
    	String apiUrl = "https://api.spoonacular.com/recipes/" + id + "/nutritionWidget.json?apiKey=" + apiKey;
    	
    	HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        
        try {
        	
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            try {

                 ObjectMapper objectMapper = new ObjectMapper();
                 JsonNode rootNode = objectMapper.readTree(response.body());
                 JsonNode nutrientsArray = rootNode.get("nutrients");

                 for (JsonNode nutrientNode : nutrientsArray) {
                     String name = nutrientNode.get("name").asText();
                     String amount = nutrientNode.get("amount").asText();
                     String unit = nutrientNode.get("unit").asText();
                     
                     nutrientsInfo = nutrientsInfo + name + ": " + amount + unit +"<br>";
                 }
                 
                 nutrientsInfo = nutrientsInfo + "</html>";
            	
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println("err 1");
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            System.out.println("err 2");
        }
    	
    	nutrientsWindow.setLayout(new GridLayout(1,0));
    	JLabel labelNutrients = new JLabel();
    	labelNutrients.setText(nutrientsInfo);
    	nutrientsWindow.add(labelNutrients);
    	nutrientsWindow.setSize(420, 500);
    	nutrientsWindow.setLocationRelativeTo(null);
		nutrientsWindow.setVisible(true);
		nutrientsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 	}

 	// this method contains the actions of the "favourite" button
 	private static void favouriteButton(Recipe recipe, JPanel favouriteField, JPanel favoriteOne, JPanel ingredientsField, List<Recipe> listFavourite, JButton favourite, DataBase database, Integer userID, SwitchMenu menu) {
 		if(listFavourite.contains(recipe)) {
 			database.connectDatabase();
	        database.deleteFavorite(recipe.getTitle(), userID);
	        database.close();
	        recipe.setIs_favourite("\u2606");
	        favourite.setText(recipe.getIs_favourite());
 			favouriteField.remove(favoriteOne);
			listFavourite.remove(recipe);
		}
		else {
			database.connectDatabase();
	        database.addFavorite(recipe.getTitle(), userID);
	        database.close();
	        recipe.setIs_favourite("\u2605");
	        favourite.setText(recipe.getIs_favourite());
			listFavourite.add(recipe);
			favouriteField.add(favoriteOne);
		}
 		favouriteField.revalidate();
        favouriteField.repaint();
 	}

 	// this method contains the actions of the "detail" button
 	private static void detailButton(Recipe recipe, SwitchMenu menu, JPanel ingredientsField) {
 		JFrame infoWindow = new JFrame("Recipe Information");
		JScrollPane jsp = makeInfoBox(recipe, menu.listIngredient, ingredientsField);
		infoWindow.add(jsp, BorderLayout.CENTER);
		infoWindow.setSize(420, 500);
		infoWindow.setLocationRelativeTo(null);
		infoWindow.setVisible(true);
		infoWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 	}
}
