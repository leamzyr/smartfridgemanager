package app.tse;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;

/**
 * Class for managing interaction with an SQLite database.
 */
public class DataBase {
	// Attributes
	private Connection conn;
	private String databaseUrl;
	private DatabaseMetaData meta;
	private String tableNames[] = { "Ingredient", "User", "Allergene", "Favorite"};
    //Class constructor. Initializes the database URL
	public DataBase(String fileName) {
		this.databaseUrl = "jdbc:sqlite:" + fileName;
	}
    //Converts a date to a string in the "YYYY-MM-DD" format.
    public static String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    //Converts a string representing a date in the "YYYY-MM-DD" format to a Date object.
    public static Date stringToDate(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }
    //Connects to the SQLite database.
	public void connectDatabase() {
		// We try to load the JDbc SQLite pilot before creating a database
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Le pilote JDBC SQLite n'a pas été trouvé.");
			e.printStackTrace();
			return;
		}
		// If the DataBase do not exist, created here.
		try {
			this.conn = DriverManager.getConnection(this.databaseUrl);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		if (this.conn != null) {
			System.out.println("Database connected.");
		}
	}

    //Checks if the necessary tables exist in the database.
    public void verifyTables() {
        try {
            this.meta = this.conn.getMetaData();
            for (String element : this.tableNames) {
                ResultSet tableInfo = this.meta.getTables(null, null, element, new String[]{"TABLE"});
                if (!(tableInfo.next())) {
                    createTable(element);
                }
                tableInfo.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //Creates a table in the database.
    public void createTable(String tableName) {
        String sqlRequest;
        Statement stmt;

        switch (tableName) {
            case "User":
                sqlRequest = "CREATE TABLE \"" + tableName + "\" (id INTEGER PRIMARY KEY, username TEXT NOT NULL);";
                break;
            case "Ingredient":
                sqlRequest = "CREATE TABLE \"" + tableName + "\" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, expire_date TEXT, amount INTEGER NOT NULL, " +
                        "unit STRING NOT NULL, user_id INTEGER NOT NULL, " +
                        "FOREIGN KEY(user_id) REFERENCES User(id));";
                break;
            case "Allergene":
            	sqlRequest = "CREATE TABLE \"" + tableName + "\" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, user_id INTEGER NOT NULL, " +
                        "FOREIGN KEY(user_id) REFERENCES User(id));";
            case "Favorite":
                sqlRequest = "CREATE TABLE \"" + tableName + "\" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, user_id INTEGER NOT NULL, " +
                        "FOREIGN KEY(user_id) REFERENCES User(id));";
                break;
            default:
                sqlRequest = "CREATE TABLE \"" + tableName + "\"(id INTEGER PRIMARY KEY);";
                break;
        }
        try {
            stmt = this.conn.createStatement();
            stmt.execute(sqlRequest);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void executeUpdate(String sqlRequest) {
        try (Statement stmt = this.conn.createStatement()) {
            stmt.execute(sqlRequest);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<String> executeQueryList(String sqlRequest) {
        ArrayList<String> resultList = new ArrayList<>();
        try (Statement stmt = this.conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sqlRequest)) {

            while (resultSet.next()) {
                resultList.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultList;
    }
	public void addUser(String username) {
	    executeUpdate("INSERT INTO User (username) VALUES ('" + username + "')");
	    System.out.println("User " + username + " successfully added to the database.");
	}

	public void deleteUser(SimpleEntry<Integer, String> user) {
		executeUpdate("DELETE FROM Ingredient WHERE user_id = " + user.getKey());
		executeUpdate("DELETE FROM Allergene WHERE user_id = " + user.getKey());
		executeUpdate("DELETE FROM Favorite WHERE user_id = " + user.getKey());
		executeUpdate("DELETE FROM User WHERE id = " + user.getKey());
		System.out.println("User #" + user.getKey() + " | " + user.getValue() + " successfully deleted from the database.");
	}

	public ArrayList<SimpleEntry<Integer, String>> getUsers() {
	    String sqlRequest = "SELECT * FROM User";
	    ArrayList<SimpleEntry<Integer, String>> userList = new ArrayList<>();

	    try {
	        Statement stmt = this.conn.createStatement();
	        ResultSet rawUserList = stmt.executeQuery(sqlRequest);

	        while (rawUserList.next()) {
	        	Integer id = Integer.parseInt(rawUserList.getString(1));
	        	String name = rawUserList.getString(2);
	        	userList.add(new SimpleEntry<>(id, name));
	        }
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }

	    System.out.println("Users : \n" + userList);
	    
	    return userList;
	}
	
    public void addFavorite(String name, Integer userID) {
        executeUpdate("INSERT INTO Favorite (name, user_id) VALUES ('" + name + "', " + userID + " )");
        System.out.println("Favorite " + name + " successfully added.");
    }
    
    public void deleteFavorite(String name, Integer userID) {
        executeUpdate("DELETE FROM Favorite WHERE name = '" + name + "' AND user_id = " + userID);
        System.out.println("Favorite " + name + " successfully deleted.");
    }

    public ArrayList<String> getFavorites(Integer userID) {
        return executeQueryList("SELECT name FROM Favorite WHERE user_id = " + userID);
    }

    public void addAllergene(String name, Integer userID) {
        executeUpdate("INSERT INTO Allergene (name, user_id) VALUES ('" + name + "', " + userID + " )");
        System.out.println("Allergene " + name + " successfully added.");
    }
    
    public void deleteAllergene(String name, Integer userID) {
        executeUpdate("DELETE FROM Allergene WHERE name = '" + name + "' AND user_id = " + userID);
        System.out.println("Allergene " + name + " successfully deleted.");
    }
    
    public ArrayList<String> getAllergens(Integer userID) {
        return executeQueryList("SELECT name FROM Allergene WHERE user_id = " + userID);
    }
    
	public int addIngredient(String name, Date exp, int amount, Integer userID, String unit) {
		String sqlRequest, expireDate = dateToString(exp);
		Statement stmt;

	    sqlRequest = "INSERT INTO Ingredient (name, expire_date, amount, unit, user_id)\n " +
	            "VALUES \n " +
	            "  (\"" + name + "\", \"" + expireDate + "\", " + amount + ", \"" + unit + "\", " + userID + " )";

		try {
			stmt = this.conn.createStatement();
			stmt.execute(sqlRequest);
			System.out.println("Element " + name + " successfully added to the data base.");
			
			//retrieve the generated Id
			ResultSet rs = stmt.getGeneratedKeys();
			int generatedId = rs.getInt(1);
			return generatedId;
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}
	
	public void setIngredientExpirationDate(String name, Date newExpDate, Integer userID) {
	    String newExpireDate = dateToString(newExpDate);
	    executeUpdate("UPDATE Ingredient SET expire_date = '" + newExpireDate + "' " +
	            "WHERE name = '" + name + "' AND user_id = " + userID);
	    System.out.println("Expiration date for ingredient " + name + " successfully updated.");
	}

	public void setIngredientAmount(String name, int newAmount, Integer userID) {
	    executeUpdate("UPDATE Ingredient SET amount = " + newAmount + " " +
	            "WHERE name = '" + name + "' AND user_id = " + userID);
	    System.out.println("Amount for ingredient " + name + " successfully updated.");
	}

	public void setIngredientUnit(String name, String newUnit, Integer userID) {
	    executeUpdate("UPDATE Ingredient SET unit = '" + newUnit + "' " +
	            "WHERE name = '" + name + "' AND user_id = " + userID);
	    System.out.println("Unit for ingredient " + name + " successfully updated.");
	}

	public void deleteIngredient(String id, Integer userID) {
	    executeUpdate("DELETE FROM Ingredient WHERE id = '" + id + "' AND user_id = " + userID);
	    System.out.println("Ingredient successfully deleted.");
	}
//Retrieves a list of all ingredients from the Ingredient table in the database
	public ArrayList<ArrayList<String>> getIngredients(Integer userID) {
		String sqlRequest;
		Statement stmt;
		ResultSet rawIngredients;
		ArrayList<ArrayList<String>> ingredientsTable = new ArrayList<>();

		sqlRequest = "SELECT * FROM Ingredient \n " +
	    		"WHERE user_id = " + userID;

		try {
			this.conn = DriverManager.getConnection(this.databaseUrl);
			stmt = this.conn.createStatement();
			rawIngredients = stmt.executeQuery(sqlRequest);

			// Getting the number of columnsSet
			int colonneCount = rawIngredients.getMetaData().getColumnCount();

			// Display column names
			for (int i = 1; i <= colonneCount; i++) {
				System.out.print(rawIngredients.getMetaData().getColumnName(i) + "\t");
			}
			System.out.println();

			// Display rows data
			while (rawIngredients.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int i = 1; i <= colonneCount; i++) {
					System.out.print(rawIngredients.getString(i) + "\t");
					row.add(rawIngredients.getString(i));
				}
				System.out.println();
				ingredientsTable.add(row);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return ingredientsTable;
	}

//Closes the connection to the database
	public void close() {
		try {
			this.conn.close();
			System.out.println("Database disconnected.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}	
}