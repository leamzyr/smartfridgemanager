 package app.tse;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import app.tse.models.IngredientModel;
import app.tse.models.Recipe;
import app.tse.recipe_search.Search;
import app.tse.sortingMethods.SortByExpirationDate;
import net.sourceforge.jdatepicker.impl.UtilDateModel;


public class ViewCreator {
	private SortByExpirationDate sort = new SortByExpirationDate();
	private JPanel favouriteField = new JPanel();
	private JPanel ingredientsField = new JPanel();
	private static JPanel shoppingListField = new JPanel();
	private static ArrayList<ArrayList<String>> user_ingredients = new ArrayList<ArrayList<String>>(); // Liste des ingrédients spécifiques à l'utilisateur
	private static ArrayList<String> user_recipes= new ArrayList<String>(); // Liste des recettes favorites spécifiques à l'utilisateur
	
	// class method used to create and show the ingredient list page
    public JPanel createAndShowListIngredient(final SwitchMenu menu, final CardLayout cardLayout, final JPanel main) {
        MainMenu.getDataBase().connectDatabase();
        user_ingredients = MainMenu.getDataBase().getIngredients(MainMenu.getSelectedUser());
        MainMenu.getDataBase().close();
        
        // expiry warning alert
    	expiryAlert(menu);
        
    	//création de la fenêtre
        final JPanel frame = new JPanel();
        
        //Bloc saisie de texte et boutons add et clearAll
  		JPanel addIngredient = new JPanel(new FlowLayout());
  		addIngredient.setBounds(0,0,400,36);
        addIngredient.setBackground(Color.gray);
  		final JTextField textField = new JTextField();
  		textField.setPreferredSize(new Dimension(200,28));
  		JButton button = new JButton("Add");
        addIngredient.add(textField);
        addIngredient.add(button);
       
        //Bloc liste ingredients
        getIngredientsField().setLayout(new BoxLayout(getIngredientsField(), BoxLayout.Y_AXIS));
        frame.add(getIngredientsField(), BorderLayout.CENTER);
        JScrollPane jsp = new JScrollPane(getIngredientsField(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.setLayout(new BorderLayout());
        frame.add(addIngredient, BorderLayout.NORTH);
        frame.add(jsp, BorderLayout.CENTER);
        CreateClearAll(menu, addIngredient);
        
        //block changement de page
        JPanel navigation = new JPanel();
        navigation.setBounds(0,0,400,36);
        navigation.setBackground(Color.gray);
        JButton backMainMenu = new JButton("Back to Main Menu");
        navigation.add(backMainMenu);
        frame.add(navigation, BorderLayout.SOUTH);
       
        for (ArrayList<String> ingredient : user_ingredients) {
        	int id = Integer.valueOf(ingredient.get(0));
            String ingredientName = ingredient.get(1);
            int amount = Integer.parseInt(ingredient.get(3));
            String quantity=ingredient.get(3);
            Date date1 = null;
			try {
				date1 = DataBase.stringToDate(ingredient.get(2));
			} catch (ParseException e) {
				e.printStackTrace();
			}
            Unit unit = Unit.valueOf(ingredient.get(4));
            UtilDateModel model = new UtilDateModel();
            model.setValue(date1);
            IngredientModel ingredientModel = new IngredientModel(id, "", ingredientName, amount, unit, date1);
            menu.listIngredient.add(ingredientModel);
            IngredientGraphic ingredientGraphic = new IngredientGraphic(ingredientName,quantity, unit, getIngredientsField(), menu.listIngredient, model ,MainMenu.getDataBase());
        }
        
        // Bouton pour ajouter un ingredient
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	//le contenu du champ de texte est récupéré
                String text = textField.getText();
                if (text.isEmpty()) {
                	JOptionPane.showMessageDialog(null, "Input field is empty, please write something");
                }
                else {
                	// Choisir un ingrédient qui existe sur Spoonacular
                	text = IngredientAutocomplete.Autocomplete(text);
                    textField.setText("");
                	if (text != null){
                		if(!text.equals("Error")) {
                			UtilDateModel model = new UtilDateModel();
                		    // Set default date to next week
            		        Calendar nextWeek = Calendar.getInstance();
            		        nextWeek.add(Calendar.DAY_OF_MONTH, 7);
            		        model.setDate(nextWeek.get(Calendar.YEAR), nextWeek.get(Calendar.MONTH), nextWeek.get(Calendar.DAY_OF_MONTH));
            		        model.setSelected(true);
            		        
                			// adding the ingredient to the data base
	              	        MainMenu.getDataBase().connectDatabase();
	              	        int id = MainMenu.getDataBase().addIngredient(text, (Date) model.getValue(), 1, MainMenu.getSelectedUser(), Unit.piece.toString());
	              	        MainMenu.getDataBase().close();
                			
                			IngredientModel ingredient = new IngredientModel(id, "", text, 1, Unit.piece, (Date) model.getValue());
              	        	menu.listIngredient.add(ingredient);
              	        	String quantity="1";
              	        	IngredientGraphic ingredient_ = new IngredientGraphic(text, quantity, Unit.piece, getIngredientsField(), menu.listIngredient, model,MainMenu.getDataBase());
              	            getIngredientsField().revalidate();
              	            getIngredientsField().repaint();
              	            textField.setText("");
              	        }
                		else {
                			JOptionPane.showMessageDialog(null, "Invalid ingredient");
                		}
                	}
                	else {
                		JOptionPane.showMessageDialog(null, "Action aborted");
                	}
                }
            }
        });
        
        // Touche "Entree" pour ajouter un ingredient
        textField.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	//le contenu du champ de texte est récupéré
                String text = textField.getText();
                if (text.isEmpty()) {
                	JOptionPane.showMessageDialog(null, "Input field is empty, please write something");
                }
                else {
                	// Choisir un ingrédient qui existe sur Spoonacular
                	text = IngredientAutocomplete.Autocomplete(text);
                    textField.setText("");
                	if (text != null){
                		if(!text.equals("Error")) {
                			UtilDateModel model = new UtilDateModel();
                		    // Set default date to next week
            		        Calendar nextWeek = Calendar.getInstance();
            		        nextWeek.add(Calendar.DAY_OF_MONTH, 7);
            		        model.setDate(nextWeek.get(Calendar.YEAR), nextWeek.get(Calendar.MONTH), nextWeek.get(Calendar.DAY_OF_MONTH));
            		        model.setSelected(true);
            		        
                			// adding the ingredient to the data base
	              	        MainMenu.getDataBase().connectDatabase();
	              	        int id = MainMenu.getDataBase().addIngredient(text, (Date) model.getValue(), 1, MainMenu.getSelectedUser(), Unit.piece.toString());
	              	        MainMenu.getDataBase().close();
                			
                			IngredientModel ingredient = new IngredientModel(id, "", text, 1, Unit.piece, (Date) model.getValue());
              	        	menu.listIngredient.add(ingredient);
              	        	String quantity="1";
              	        	IngredientGraphic ingredient_ = new IngredientGraphic(text, quantity, Unit.piece, getIngredientsField(), menu.listIngredient, model,MainMenu.getDataBase());
              	            getIngredientsField().revalidate();
              	            getIngredientsField().repaint();
              	            textField.setText("");
              	        }
                		else {
                			JOptionPane.showMessageDialog(null, "Invalid ingredient");
                		}
                	}
                	else {
                		JOptionPane.showMessageDialog(null, "Action aborted");
                	}
                }
            }
        });
        
        backMainMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		EraseAndSwitch(cardLayout, main);
            }
        });
        
        frame.setSize(400, 500);
        return frame;
    }
    
    // class method used to create and show the recipe list page
    public JPanel createAndShowListRecipe(final SwitchMenu menu, final CardLayout cardLayout, final JPanel main) {
    	//création de la fenêtre
    	final JPanel frame = new JPanel();
    	
        //Bloc recipes list
        final JPanel recetteField = new JPanel();
        recetteField.setLayout(new BoxLayout(recetteField, BoxLayout.Y_AXIS));
        frame.add(recetteField, BorderLayout.CENTER);
        JScrollPane jsp = new JScrollPane(recetteField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.setLayout(new BorderLayout());
        frame.add(jsp, BorderLayout.CENTER);
        
        //block change display settings (after recipes list, otherwise recetteField will overwhelm settings)
        JPanel settings = new JPanel();
        settings.setPreferredSize(new Dimension(500, 68));
        
        // For info about total results
        JPanel infoTotalRecipe = new JPanel();
        String textToLabel = "Click 'Liste Recette' to discover your next favorite meal !";
        JLabel totalLabel = new JLabel(textToLabel);
        infoTotalRecipe.add(totalLabel);
        
        settings.setBackground(Color.gray);
        JPanel setNumber = new JPanel();
        JLabel label = new JLabel("Set number of results : ");
        JTextArea number = new JTextArea("10");
        setNumber.add(label);
        setNumber.add(number);
        settings.add(setNumber);
  		JButton ListeRecette = new JButton("Find recipes");
  		settings.add(ListeRecette);
  		JButton sortByExpirationDate = new JButton("Sort");
  		settings.add(sortByExpirationDate);
  		settings.add(infoTotalRecipe);
  		frame.add(settings, BorderLayout.NORTH);
        
        //block changement de page
  		JPanel navigation = new JPanel();
  		JButton backMainMenu = new JButton("Back to Main Menu");
        navigation.add(backMainMenu);
        frame.add(navigation, BorderLayout.SOUTH);
     
        ListeRecette.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Clear recipe list
                recetteField.removeAll();
            	recetteField.revalidate();
                recetteField.repaint();
                try {
                	int numberResults = Integer.parseInt(number.getText());
                	menu.setListRecipe(Search.RecipeSearch(menu, numberResults));
              	    
              	    //Info panel
              	    int totalResults = Search.getTotalResults();
              	    String changeLabel = "Total results : " + totalResults;
              	    if(numberResults < totalResults) { //If there is less available result than ask by the user, just display the total number of available result
              	    	changeLabel += (" | Displayed results : " + numberResults);
              	    }
              	    totalLabel.setText(changeLabel);
              	    
              	    for (int i = 0; i < menu.getListRecipe().size(); i++) {
                        recetteField.add(Recette.recette(menu.getListRecipe().get(i), menu.listIngredient, getIngredientsField(), menu.getListFavourite(), favouriteField,shoppingListField, menu, MainMenu.getDataBase(), MainMenu.getSelectedUser()));
              	    }
              	    recetteField.revalidate();
                    recetteField.repaint();
                    settings.revalidate();
                    settings.repaint();
                }
                catch (NumberFormatException ex) {
                	JOptionPane.showMessageDialog(null, "number must be an integer.");
                	number.setText("10");
                	settings.revalidate();
                    settings.repaint();
                }
            }
        });
        
        backMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cardLayout.next(main);
            }
        });
        
        sortByExpirationDate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu.setListRecipe(sort.Sort(menu, Search.getTotalResults()));
                recetteField.removeAll();
            	recetteField.revalidate();
                recetteField.repaint();
                for (int i = 0; i < menu.getListRecipe().size(); i++) {
                    recetteField.add(Recette.recette(menu.getListRecipe().get(i), menu.listIngredient, getIngredientsField(), menu.getListFavourite(), favouriteField,shoppingListField, menu, MainMenu.getDataBase(), MainMenu.getSelectedUser()));
          	    }
                recetteField.revalidate();
                recetteField.repaint();
            }
        });
        
        recetteField.revalidate();
        recetteField.repaint();
        frame.setSize(400, 500);

        return frame;
    }
    
 // Class method used to create and show the allergen list page
    public JPanel createAndShowListAllergens(final SwitchMenu menu, final CardLayout cardLayout, final JPanel main) {
        MainMenu.getDataBase().connectDatabase();

        String[] allergenNames = {"dairy", "egg", "gluten", "grain", "peanut", "seafood", "sesame", 
                                "shellfish", "soy", "sulfite", "tree nut", "wheat"};

        ArrayList<String> userAllergens = MainMenu.getDataBase().getAllergens(MainMenu.getSelectedUser());
        MainMenu.getDataBase().close();

        final JPanel frame = new JPanel();
        ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();

        // Bloc liste d'allergènes
        final JPanel allergeneField = new JPanel();
        allergeneField.setLayout(new BoxLayout(allergeneField, BoxLayout.Y_AXIS));

        for (String allergen : allergenNames) {
            JCheckBox checkBox = new JCheckBox(allergen);
            checkBoxes.add(checkBox);
            allergeneField.add(checkBox);

            if (userAllergens.contains(allergen)) {
                checkBox.setSelected(true);
            }
        }

        frame.add(allergeneField, BorderLayout.CENTER);
        JScrollPane jsp = new JScrollPane(allergeneField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.setLayout(new BorderLayout());
        frame.add(jsp, BorderLayout.CENTER);
        if (menu.getListAllergens() != null) {
            for (JCheckBox checkBox : checkBoxes) {
                if (menu.getListAllergens().contains(checkBox.getText())) {
                    checkBox.setSelected(true);
                }
            }
        }
        for (JCheckBox checkBox : checkBoxes) {
        	allergeneField.add(checkBox);
        }
   
        JPanel navigation = new JPanel();
        navigation.setBounds(0,0,400,36);
        navigation.setBackground(Color.gray);
  		JButton clearAll = new JButton("Reset Allergens");
  		navigation.add(clearAll);
  		JButton backMainMenu = new JButton("Back to Main Menu");
        navigation.add(backMainMenu);
        frame.add(navigation, BorderLayout.SOUTH);
     
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    MainMenu.getDataBase().connectDatabase();
                    MainMenu.getDataBase().addAllergene(source.getText(), MainMenu.getSelectedUser());
                    MainMenu.getDataBase().close();
                    menu.getListAllergens().add(source.getText());
                    System.out.println("debug : cet allergène a été ajouté : " + source.getText());
                } else {
                    MainMenu.getDataBase().connectDatabase();
                    MainMenu.getDataBase().deleteAllergene(source.getText(), MainMenu.getSelectedUser());
                    MainMenu.getDataBase().close();
                    menu.getListAllergens().remove(source.getText());
                    System.out.println("debug : cet allergène a été enlevé : " + source.getText());
                }
            }
        };
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.addItemListener(itemListener);
        }
        clearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (JCheckBox checkBox: checkBoxes) {
            		checkBox.setSelected(false);
            	}
            }
        });
        
        backMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.next(main);
            }
        });
        allergeneField.revalidate();
        allergeneField.repaint();
        frame.setSize(400, 500);
        
        return frame;
    }
    
    public JPanel createAndShowShoppingList(final SwitchMenu menu, final CardLayout cardLayout, final JPanel main) {
        final JPanel frame = new JPanel();
        
        // Créez un JPanel pour la liste de courses (similaire à la méthode createAndShowListIngredient)
        shoppingListField.setLayout(new BoxLayout(shoppingListField, BoxLayout.Y_AXIS));
        frame.add(shoppingListField, BorderLayout.CENTER);
        JScrollPane jsp = new JScrollPane(shoppingListField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.setLayout(new BorderLayout());
        frame.add(jsp, BorderLayout.CENTER);
        
        // Bloc changement de page pour revenir au menu principal
        JPanel navigation = new JPanel();
        navigation.setBounds(0, 0, 400, 36);
        navigation.setBackground(Color.gray);
        JButton backMainMenu = new JButton("Back to Main Menu");
        JButton exportShoppingList = new JButton("Export Shopping List");
        navigation.add(exportShoppingList);
        navigation.add(backMainMenu);
        frame.add(navigation, BorderLayout.SOUTH);
        exportShoppingList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportShoppingList(shoppingListField);
            }
        });
        
        backMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.next(main);
            }
        });
        
        frame.setSize(400, 500);
        return frame;
    }
    
    // export list format .txt
    private void exportShoppingList(JPanel shoppingListField) {
        try {
            FileWriter writer = new FileWriter("shopping_list.txt");
            for (Component component : shoppingListField.getComponents()) {
                if (component instanceof JLabel) {
                    String ingredientName = ((JLabel) component).getText().substring(2);
                    writer.write(ingredientName + System.lineSeparator());
                }
            }
            writer.close();
            JOptionPane.showMessageDialog(null, "Shopping List exported");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting Shopping List");
        }
    }
    
    // Méthode pour ajouter les ingrédients d'une recette à la liste de courses
    public void addIngredientsToShoppingList(Recipe recipe, JPanel shoppingListField) {
        // Parcourez la liste des ingrédients de la recette
        for (IngredientModel ingredient : recipe.getIngredientList()) {
            // Ajoutez chaque ingrédient à la liste de courses (vous pouvez personnaliser cette partie selon vos besoins)
            JLabel ingredientLabel = new JLabel("- " + ingredient.getName());
            shoppingListField.add(ingredientLabel);
        }
        
        // Rafraîchissez l'affichage de la liste de courses
        shoppingListField.revalidate();
        shoppingListField.repaint();
    }
    
    // class method used to clear ingredient list
    public void CreateClearAll(final SwitchMenu menu, JPanel addIngredient) {
    	JButton clearAll = new JButton("Clear List");
 		addIngredient.add(clearAll);
    	// Bouton pour effacer toute la liste
        clearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Component[] components = getIngredientsField().getComponents();
                for (Component component : components) {
                    if (component instanceof JPanel) {
                        getIngredientsField().remove(component);
          	            MainMenu.getDataBase().connectDatabase();
          	            MainMenu.getDataBase().deleteIngredient(component.getName(),MainMenu.getSelectedUser());
          	            MainMenu.getDataBase().close();
                    }
                }
                menu.listIngredient.clear();
                getIngredientsField().revalidate();
                getIngredientsField().repaint();
                System.out.println("clear");
                System.out.println("size objects list : " + menu.listIngredient.size() + " size graphical list : " + getIngredientsField().getComponentCount());
            }
        });
    }
    
    // class method used to create and show the favourite recipes page
    public JPanel createAndShowListFavourites(final SwitchMenu menu, final CardLayout cardLayout, final JPanel main) {
    	MainMenu.getDataBase().connectDatabase();
        user_recipes = MainMenu.getDataBase().getFavorites(MainMenu.getSelectedUser());
        System.out.println(user_recipes);
        MainMenu.getDataBase().close();
    	
    	// creating the window
    	final JPanel frame = new JPanel();
    	
        // Favourite recipe list
        final JPanel favouriteField = new JPanel();
        favouriteField.setLayout(new BoxLayout(favouriteField, BoxLayout.Y_AXIS));
        frame.add(favouriteField, BorderLayout.CENTER);
        JScrollPane jsp = new JScrollPane(favouriteField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.setLayout(new BorderLayout());
        frame.add(jsp, BorderLayout.CENTER);
        JPanel navigation = new JPanel();
        navigation.setBounds(0,0,400,36);
        navigation.setBackground(Color.gray);
        JButton backMainMenu = new JButton("Back to Main Menu");
        navigation.add(backMainMenu);
        frame.add(navigation, BorderLayout.SOUTH);
        
        //Retrieve recipes from favorite list
        if(!user_recipes.isEmpty()) {
        	menu.setListFavourite(Search.RecipeRetrieve(menu, user_recipes));
        	for(Recipe recipe : menu.getListFavourite()) {
    			favouriteField.add(Recette.favorite(recipe, menu.listIngredient, ingredientsField, menu.getListFavourite(), favouriteField,shoppingListField, menu, MainMenu.getDataBase(), MainMenu.getSelectedUser()));
        	}
        }
        
        favouriteField.revalidate();
        favouriteField.repaint();
        this.favouriteField = favouriteField;
        
        backMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.next(main);
            }
        });
        
        frame.setSize(400, 500);
        return frame;
    }
    
 // this method displays an alert message when an item in the fridge is expired, or about to be
    private void expiryAlert(final SwitchMenu menu) {
        List<IngredientModel> ingredientList = new ArrayList<>();
        for (ArrayList<String> ingredient : user_ingredients) {
            String ingredientName = ingredient.get(1);
            int amount = Integer.parseInt(ingredient.get(3));
            Date date1 = null;
			try {
				date1 = DataBase.stringToDate(ingredient.get(2));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Unit unit = Unit.valueOf(ingredient.get(4));
            UtilDateModel model = new UtilDateModel();
            model.setValue(date1);
            IngredientModel ingredientModel = new IngredientModel(menu.listIngredient.size() + 1, "", ingredientName, amount, unit, date1);
            ingredientList.add(ingredientModel);
        }

        List<IngredientModel> expiredList = new ArrayList<>();
        Date currentDate = Date.from(Instant.now().plus(3, ChronoUnit.DAYS)); // currentDate represents the date three days from now

        for (IngredientModel ingredient : ingredientList) {
            Date expiryDate = ingredient.getExpirationDate();

            if (expiryDate != null && expiryDate.before(currentDate)) {
                expiredList.add(ingredient);
            }
        }
        if (!expiredList.isEmpty()) {
        	String message = "Some of your items are expired or about to expire:";
        	for (IngredientModel ingredient : expiredList) {
        		message += "\n\t- " + ingredient.getName() + " | " + ingredient.getExpirationDate().toString();
        	}
            JOptionPane.showMessageDialog(null, message, "Expiry Alert", JOptionPane.WARNING_MESSAGE);
        }
    }

	public JPanel getIngredientsField() {
		return ingredientsField;
	}
	private void EraseAndSwitch(CardLayout cardLayout, JPanel main) {
		ingredientsField.removeAll();
		cardLayout.next(main);
	}
	
}
