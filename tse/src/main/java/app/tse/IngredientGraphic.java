package app.tse;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import app.tse.models.IngredientModel;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class IngredientGraphic {
	public IngredientGraphic (String text,String quantity_, Unit unit2, final JPanel ingredientsField, final List<IngredientModel> ingredientList,UtilDateModel model, DataBase database){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		final JLabel quantity = new JLabel(quantity_);
		JTextArea unit = new JTextArea(String.valueOf(unit2));
    	quantity.setBorder(new EmptyBorder(0, 5, 0, 5));
    	final JLabel name = new JLabel(text);
    	JButton plus = new JButton("+");
  		JButton minus = new JButton("\u2212");
  		JButton delete = new JButton("X");
  		final JPanel p = new JPanel();
       	p.setLayout(new GridBagLayout());
       	p.setPreferredSize(new Dimension(0,40));
        p.add(minus);
        p.add(quantity);
        p.add(unit);
        p.add(plus);
        p.add(name);	
        name.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        model.setSelected(true);
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
//        datePicker.setMinimumSize(new Dimension(100, 40));
        datePicker.setBorder(new EmptyBorder(0, 0, 0, 10));
        p.add(datePicker);
        
        p.add(delete);
        

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getMinimumSize().height));
        
        ingredientsField.add(p);
        
        // get the index of ingredient to sync with object list
        final int index = ingredientsField.getComponentZOrder(p);
        
        plus.addActionListener(new ActionListener() {
        	
        	public void actionPerformed(ActionEvent e) {
        		final int index = ingredientsField.getComponentZOrder(p);
        		String quantityText = quantity.getText();
        		int intValue = Integer.parseInt(quantityText) + 1;
        		quantity.setText(Integer.toString(intValue));
        		ingredientList.get(index).setQuantity(intValue);
  	          	database.connectDatabase();
  	          	database.setIngredientAmount(ingredientList.get(index).getName(),intValue,MainMenu.getSelectedUser());
  	          	database.close();
        	}
        });
        
        minus.addActionListener(new ActionListener() {
        	
        	public void actionPerformed(ActionEvent e) {
        		final int index = ingredientsField.getComponentZOrder(p);
        		String quantityText = quantity.getText();
        		if (!quantityText.equals("0")){
        				int intValue = Integer.parseInt(quantityText) - 1;
        				quantity.setText(Integer.toString(intValue));
        				ingredientList.get(index).setQuantity(intValue);
          	          	database.connectDatabase();
          	          	database.setIngredientAmount(ingredientList.get(index).getName(),intValue,MainMenu.getSelectedUser());
          	          	database.close();
        		}
        	}
        });
        
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	final int index = ingredientsField.getComponentZOrder(p);
  	          	database.connectDatabase();
  	          	database.deleteIngredient(String.valueOf(ingredientList.get(index).getId()),MainMenu.getSelectedUser());
  	          	database.close();
  	          	ingredientList.remove(index);
  	          	ingredientsField.remove(p);
  	          	
                ingredientsField.revalidate();
                ingredientsField.repaint();
            }
        });
        
        unit.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateUnit();
            }
            private void updateUnit() {
            	final int index = ingredientsField.getComponentZOrder(p);
            	String newUnitText = unit.getText().trim();
            	try {
            		Unit newUnit = Unit.valueOf(newUnitText);
            		ingredientList.get(index).setUnit(newUnit);
            		database.connectDatabase();
            		System.out.println(newUnitText);
      	          	database.setIngredientUnit(ingredientList.get(index).getName(), newUnitText,MainMenu.getSelectedUser());
      	          	database.close();
            	}
            	catch (IllegalArgumentException ex) {
            		unit.setText("piece");
            		ingredientList.get(index).setUnit(Unit.piece);
            		ingredientsField.revalidate();
            		ingredientsField.repaint();
            		JOptionPane.showMessageDialog(null, "Valid units are : ml, l, g, kg, piece, serving, C, c, T, tbsp(s), t, tsp(s), lb, oz, gal, qt, pt and cup(s)");
            	}
            }
        });
        
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
            	final int index = ingredientsField.getComponentZOrder(p);
                if ("value".equals(evt.getPropertyName())) {
                    // La propriété "value" a changé, mettez à jour l'expirationDate
                    Date newExpirationDate = (Date) evt.getNewValue();
                    ingredientList.get(index).setExpirationDate(newExpirationDate);
      	          	database.connectDatabase();
      	          	database.setIngredientExpirationDate(ingredientList.get(index).getName(),newExpirationDate, MainMenu.getSelectedUser()); 
      	          	database.close();
                }
            }
        });
	}
}
