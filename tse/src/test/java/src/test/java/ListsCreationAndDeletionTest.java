package src.test.java;

import static org.junit.Assert.assertEquals;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

import app.tse.DataBase;
import app.tse.IngredientGraphic;
import app.tse.ViewCreator;
import app.tse.SwitchMenu;
import app.tse.Unit;
import app.tse.models.IngredientModel;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class ListsCreationAndDeletionTest {
	
	
	private ViewCreator view;
	private SwitchMenu switchMenu;
	IngredientModel ing1;
	IngredientModel ing2;
	IngredientGraphic ing1_;
	IngredientGraphic ing2_;
	Date ingredientExpirationDate1;
	Date ingredientExpirationDate2;
	Calendar today;
	Calendar nextWeek;
	UtilDateModel model;
	private DataBase BDD;

    @Before
    public void setUp() {
    	this.BDD = new DataBase("hihihaha.db");
		BDD.connectDatabase();
        view = new ViewCreator();
        switchMenu = new SwitchMenu();
        switchMenu.listIngredient = new ArrayList<IngredientModel>();
        ingredientExpirationDate1 = new Date();
        nextWeek = Calendar.getInstance();
        nextWeek.add(Calendar.DAY_OF_MONTH, -7);
        ingredientExpirationDate2 = nextWeek.getTime();
        today = Calendar.getInstance();
        model = new UtilDateModel();
        model.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        model.setSelected(true);
		ing1 = new IngredientModel(0,"image","apple", 1, Unit.piece, ingredientExpirationDate1);
		ing2 = new IngredientModel(1, "image2", "pear", 3, Unit.piece, ingredientExpirationDate2);
		switchMenu.listIngredient.add(ing1);
		switchMenu.listIngredient.add(ing2);
		ing1_ = new IngredientGraphic("apple", "1", Unit.piece, view.getIngredientsField(), switchMenu.listIngredient, model, BDD);
		ing2_ = new IngredientGraphic("pear", "1", Unit.piece, view.getIngredientsField(), switchMenu.listIngredient, model, BDD);
    }    
    
	@Test
	public void testCreationIngredient() {
		// Check if both graphical and object lists contain one ingredient
		assertEquals(2, switchMenu.listIngredient.size());
		assertEquals(2, view.getIngredientsField().getComponentCount());
		
		// Check if the ingredient inside both lists are the right ingredient
		assertEquals(ing1, switchMenu.listIngredient.get(0));
		assertEquals(ing2, switchMenu.listIngredient.get(1));
//		assertEquals(ingredientsField.getComponent(0), ing1_);
		
	}
	
	@Test
	public void testDeletionIngredient() {
	    JPanel ingredientPanelToDelete = (JPanel) view.getIngredientsField().getComponent(0);
	    
	    // Find the delete button within the panel
	    JButton deleteButton = null;
	    for (Component component : ingredientPanelToDelete.getComponents()) {
	        if (component instanceof JButton && ((JButton) component).getText().equals("X")) {
	            deleteButton = (JButton) component;
	            break;
	        }
	    }

	    // Trigger the ActionListener for the delete button
	    if (deleteButton != null) {
	        for (ActionListener listener : deleteButton.getActionListeners()) {
	            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "delete"));
	        }
	    }

	    // Check if the ingredient was removed from both lists after deletion
	    assertEquals(1, switchMenu.listIngredient.size());
	    assertEquals(1, view.getIngredientsField().getComponentCount());
	}
	
	@Test
	public void testClearAll() {
		JPanel forClearAll = new JPanel();
		view.CreateClearAll(switchMenu, forClearAll);
        // Find the clearAll button within the frame
        JButton clearAll = null;
        for (Component component : forClearAll.getComponents()) {
            if (component instanceof JButton && ((JButton) component).getText().equals("Clear List")) {
                clearAll= (JButton) component;
                break;
            }
        }

        // Trigger the ActionListener for the clearAll button
        if (clearAll != null) {
            for (ActionListener listener : clearAll.getActionListeners()) {
            	System.out.println("click simulated");
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "clearAll"));
            }
        }

        // Check if the ingredients were removed from both lists and graphical field after clearAll
        assertEquals(0, switchMenu.listIngredient.size());
        assertEquals(0, view.getIngredientsField().getComponentCount());
    }
	
	@Test
	public void testAddQuantity() {
		JPanel ingredientPanelToIncrement = (JPanel) view.getIngredientsField().getComponent(0);
	    
	    // Find the delete button within the panel
	    JButton plus = null;
	    for (Component component : ingredientPanelToIncrement.getComponents()) {
	        if (component instanceof JButton && ((JButton) component).getText().equals("+")) {
	            plus = (JButton) component;
	            break;
	        }
	    }

	    // Trigger the ActionListener for the delete button
	    if (plus != null) {
	        for (ActionListener listener : plus.getActionListeners()) {
	            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "plus"));
	        }
	    }

	    // Check if the ingredient was removed from both lists after deletion
	    assertEquals(2, switchMenu.listIngredient.get(0).getQuantity());
	}
	@Test
	public void testExpirationDate() {
		assertEquals(ingredientExpirationDate1, switchMenu.listIngredient.get(0).getExpirationDate());
//		JPanel ingre = (JPanel) listeIngredient.ingredientsField.getComponent(0);
//		assertEquals(switchMenu.listIngredient.get(0).getExpirationDate(), ing1_.);
	}
	
	@Test
	public void testSortByExpDate() {
		
		assertEquals(-1, ing1.compareTo(ing2));
	}
	
}
