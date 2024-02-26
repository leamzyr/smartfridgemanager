package app.tse;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

public class MainMenu {
    private static ArrayList<SimpleEntry<Integer, String>> users = new ArrayList<>(); // Liste des utilisateurs
    private static DataBase dataBase= new DataBase("fridgeApplication.db");
    private static Integer selectedUser; //Temps que le listener n'est pas implémenté
    private static int numberOfUsersDisplayed = 0; // maximal number of 6
    
    public static DataBase getDataBase() {
		return dataBase;
	}
	public static Integer getSelectedUser() {
		return selectedUser;
	}
	
    public static JPanel createAndShowGUI(CardLayout cardLayout, JPanel main, SwitchMenu menu, ViewCreator view, JTabbedPane tabbedPane) {
    	JPanel globalInterface = new JPanel();
    	globalInterface.setBackground(new Color(20, 20, 20));
    	JPanel usersPanel = new JPanel();
    	
    	JPanel usersSubPanel = new JPanel();
    	usersSubPanel.setLayout(new GridLayout(2, 3));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Who's cooking ?");
        welcomeLabel.setFont(new Font("Arial", 0, 36));
        welcomeLabel.setForeground(new Color(240, 240, 240));
        welcomePanel.add(welcomeLabel);
        
    	dataBase.connectDatabase();
    	users=dataBase.getUsers();
    	dataBase.close();
    	while (users.size() > 6) {
    		users.removeLast();
    	}
    	
    	setUsers(usersSubPanel, cardLayout, main, menu, view, tabbedPane);
    	
        GroupLayout layout = new GroupLayout(globalInterface);
        globalInterface.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usersSubPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(welcomePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usersSubPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dataBase.close();
        }));
        return globalInterface;
    }

    private static void setUsers(JPanel usersPanel, CardLayout cardLayout, JPanel main, SwitchMenu menu, ViewCreator view, JTabbedPane tabbedPane) {
    	usersPanel.removeAll(); // Effacer les boutons existants
    	numberOfUsersDisplayed = 0;
    	
        for (SimpleEntry<Integer, String> user : users) {
            MouseListener selectUserMouseListener = new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                	selectedUser=user.getKey();
                    cardLayout.next(main);
                    tabbedPane.removeAll();
                    menu.getListRecipe().clear();
                    menu.listIngredient.clear();
                    menu.getListAllergens().clear();
                    menu.getListFavourite().clear();
                    tabbedPane.addTab("Ingredients", view.createAndShowListIngredient(menu, cardLayout, main));
                    tabbedPane.addTab("Recipes", view.createAndShowListRecipe(menu, cardLayout, main));
                    tabbedPane.addTab("Allergens", view.createAndShowListAllergens(menu, cardLayout, main));
                    tabbedPane.addTab("Shopping list", view.createAndShowShoppingList(menu, cardLayout, main));
                    tabbedPane.addTab("Favourites", view.createAndShowListFavourites(menu, cardLayout, main));
                }
            };
            
            JPanel userPanel = new JPanel(); // Panneau pour chaque utilisateur
            JPanel userPicture = new JPanel();
            JLabel name = new JLabel();
            JButton deleteButton = new JButton();
    		BufferedImage addButtonImage;
            BufferedImage deleteImage;

            userPanel.setPreferredSize(new Dimension(200, 225));
            userPanel.setBackground(new Color(20, 20, 20));

            userPicture.setPreferredSize(new Dimension(190, 190));
            userPicture.setBackground(new Color(20, 20, 20));
    		try {
    			File usernameImageFolder = new File("./img/" + user.getValue() + ".png");
    			if(usernameImageFolder.exists() && !usernameImageFolder.isDirectory()) { 
        			addButtonImage = ImageIO.read(usernameImageFolder);
    			} else {
    				addButtonImage = ImageIO.read(new File("./img/User" + numberOfUsersDisplayed + ".png"));
    			}
    	    	JLabel picLabel = new JLabel(new ImageIcon(addButtonImage));
    	    	
    	        GroupLayout userPictureLayout = new GroupLayout(userPicture);
    	        userPicture.setLayout(userPictureLayout);
    	        userPictureLayout.setHorizontalGroup(
    	        		userPictureLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	            .addGroup(userPictureLayout.createSequentialGroup()
    	                .addContainerGap()
    	                .addComponent(picLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    	                .addContainerGap())
    	        );
    	        userPictureLayout.setVerticalGroup(
    	        		userPictureLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	            .addGroup(userPictureLayout.createSequentialGroup()
    	                .addContainerGap()
    	                .addComponent(picLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    	                .addContainerGap())
    	        );
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            name.setHorizontalAlignment(SwingConstants.CENTER);
            name.setText(user.getValue());
            name.setFont(new Font("Arial", 0, 18));
            name.setForeground(new Color(128, 128, 128));
            name.setPreferredSize(new Dimension(190, 20));
            
            deleteButton.setPreferredSize(new Dimension(20, 20));
            deleteButton.setBorderPainted(false); 
            deleteButton.setContentAreaFilled(false); 
            deleteButton.setFocusPainted(false); 
            deleteButton.setOpaque(false);
            //deleteButton.setForeground(new Color(128, 128, 128));
    		try {
    			deleteImage = ImageIO.read(new File("./img/delete.png"));
    	    	deleteButton.setIcon(new ImageIcon(deleteImage));
	            deleteButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                	dataBase.connectDatabase();
	                    dataBase.deleteUser(user);
	                    dataBase.close();
	                    users.remove(user);
	                    setUsers(usersPanel, cardLayout, main, menu, view, tabbedPane); // Mettre à jour après la suppression
	                }
	            });
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            GroupLayout userLayout = new GroupLayout(userPanel);
            userPanel.setLayout(userLayout);
            userLayout.setHorizontalGroup(
                userLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(userLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(userPicture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
            userLayout.setVerticalGroup(
                userLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(userLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(userPicture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            
            userPanel.addMouseListener(selectUserMouseListener);
            usersPanel.add(userPanel); // Ajout du panneau de l'utilisateur
            ++numberOfUsersDisplayed;
        }
        
        
        // putting an add user button if necessary
        if (numberOfUsersDisplayed < 6) {
        	JPanel addUser = getAddUserButton(usersPanel, cardLayout, main, menu, view, tabbedPane);
        	usersPanel.add(addUser);
        	++numberOfUsersDisplayed;
        }
        
        // filling empty places with icons
        while (numberOfUsersDisplayed < 6) {
        	usersPanel.add(getEmptyBox());
        	++numberOfUsersDisplayed;
        }
        
        usersPanel.revalidate(); // Rafraîchir l'affichage
        usersPanel.repaint();
    }
    
    private static JPanel getAddUserButton(JPanel usersPanel, CardLayout cardLayout, JPanel main, SwitchMenu menu, ViewCreator view, JTabbedPane tabbedPane) {
        MouseListener addUserMouseListener = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                String username = JOptionPane.showInputDialog(usersPanel, "Enter username:");
                if (username != null && !username.isEmpty()) {
                	dataBase.connectDatabase();
                    dataBase.addUser(username);
                    dataBase.close();
                    if (users.isEmpty()) {
                        users.add(new SimpleEntry<>(1, username));
                    } else {
                        users.add(new SimpleEntry<>(users.getLast().getKey() + 1, username));
                    }
                    setUsers(usersPanel, cardLayout, main, menu, view, tabbedPane);
                }
            }
        };
        
    	JPanel addUser = new JPanel();
    	JPanel addUserImage = new JPanel();
    	JLabel addUserText = new JLabel();
    	
    	addUser.setPreferredSize(new Dimension(200, 225));
    	addUser.setBackground(new Color(20, 20, 20));

		addUserImage.setPreferredSize(new Dimension(190, 190));
		addUserImage.setBackground(new Color(20, 20, 20));		
		BufferedImage addButtonImage;
		try {
			addButtonImage = ImageIO.read(new File("./img/addUser.png"));
	    	JLabel picLabel = new JLabel(new ImageIcon(addButtonImage));
	    	
	        GroupLayout addUserImageLayout = new GroupLayout(addUserImage);
	        addUserImage.setLayout(addUserImageLayout);
	        addUserImageLayout.setHorizontalGroup(
	        		addUserImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(addUserImageLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(picLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addContainerGap())
	        );
	        addUserImageLayout.setVerticalGroup(
	        		addUserImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(addUserImageLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(picLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addContainerGap())
	        );
		} catch (IOException e) {
			e.printStackTrace();
		}

		addUserText.setHorizontalAlignment(SwingConstants.CENTER);
		addUserText.setText("Add Profile");
		addUserText.setFont(new Font("Arial", 0, 18));
		addUserText.setForeground(new Color(128, 128, 128));
		addUserText.setPreferredSize(new Dimension(190, 20));


        GroupLayout addUserLayout = new GroupLayout(addUser);
        addUser.setLayout(addUserLayout);
        addUserLayout.setHorizontalGroup(
        		addUserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(addUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addUserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(addUserImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addUserText, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        addUserLayout.setVerticalGroup(
        		addUserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(addUserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addUserImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addUserText, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
		
        addUser.addMouseListener(addUserMouseListener);
        
        return addUser;
    }
    
    private static JPanel getEmptyBox() {
    	JPanel emptyBox = new JPanel(); // Panneau pour chaque utilisateur

    	emptyBox.setPreferredSize(new Dimension(200, 225));
    	emptyBox.setBackground(new Color(20, 20, 20));
               
        return emptyBox;
    }
}
