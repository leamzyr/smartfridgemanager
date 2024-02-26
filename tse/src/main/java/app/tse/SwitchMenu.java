package app.tse;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import app.tse.models.*;

public class SwitchMenu {
	
	public List<IngredientModel> listIngredient = new ArrayList<IngredientModel>();
	private List<Recipe> listRecipe = new ArrayList<Recipe>();
	private ArrayList<String> listAllergens = new ArrayList<String>();
	private List<Recipe> listFavourite = new ArrayList<Recipe>();

    public static void main(String[] args) {
    	DataBase dataBase = new DataBase("fridgeApplication.db");
    	dataBase.connectDatabase();
    	dataBase.verifyTables();
    	dataBase.close();
    	
    	JFrame frame = new JFrame("Smart Fridge App");
    	CardLayout cardLayout = new CardLayout();
    	JPanel main = new JPanel(cardLayout);
    	SwitchMenu menu = new SwitchMenu();
    	ViewCreator view = new ViewCreator();
    	frame.add(main);
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	JPanel mainMenu = MainMenu.createAndShowGUI(cardLayout, main, menu, view, tabbedPane);
        main.add(mainMenu);
        main.add(tabbedPane);
      
        frame.setSize(880, 710);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private static Component test() {
    	JOptionPane.showMessageDialog(null, "TEST", "TEST", JOptionPane.WARNING_MESSAGE);
    	return new JPanel();
    }

	public List<Recipe> getListFavourite() {
		return listFavourite;
	}

	public void setListFavourite(List<Recipe> listFavourite) {
		this.listFavourite = listFavourite;
	}

	public ArrayList<String> getListAllergens() {
		return listAllergens;
	}

	public void setListAllergens(ArrayList<String> listAllergens) {
		this.listAllergens = listAllergens;
	}

	public List<Recipe> getListRecipe() {
		return listRecipe;
	}

	public void setListRecipe(List<Recipe> listRecipe) {
		this.listRecipe = listRecipe;
	}
}