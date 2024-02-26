# info10

## Smart Fridge Manager

The Smart Fridge Manager project aims to help users efficiently manage the contents of their fridge and plan meals effectively. This application enables users to track ingredients, find recipes based on available items, and reduce food waste.

## Authors
- [Simon AUTECHAUD](https://devops.telecomste.fr/mazoyer.lea)  
- [Alexandre CHAMBRIER](https://devops.telecomste.fr/chambrier.alexandre) 
- [Oscar CIZERON](https://devops.telecomste.fr/cizeron.oscar)  
- [Clément JACQUET](https://devops.telecomste.fr/jacquet.clement)  
- [Léa MAZOYER](https://devops.telecomste.fr/mazoyer.lea)  
- [Quentin RABAN](https://devops.telecomste.fr/raban.quentin)  

## Features

- **Ingredient Management:** Add, update, and delete ingredients in your fridge.
- **Expiration Date Tracking:** Monitor expiration dates of ingredients to reduce food waste.
- **Recipe Search:** Find recipes based on ingredients available in your fridge.
- **Meal Planning:** Plan your meals using existing ingredients.
- **Shopping List:** Create your shopping list from the ingredients of one or more selected recipes.
- **Favorite Recipes:** Mark your favorite recipes for quick access.

## Installation

### Requirements

- Java 8 installed. 
- Maven for project build.
- SQLite 3.34.0 database for data storage.
- Spoonacular API Key (Replace apiKey in the Search class with your own API key).

### Configuration

1. Clone the repository:

```bash
git clone https://github.com/yourName/info10.git
cd info10
```

2. Build the project:

```bash
mvn clean install
```

3. Configure the database:

Create a database (SQLite is recommended) and update the application.properties file with your database credentials.

4. Run the application:

```bash
mvn spring-boot:run
```

### Usage

1. Access the application via the provided URL.

2. Start by adding your ingredients to your fridge with their quantity and expiration date.

3. Explore available recipes based on your fridge content.

4. Test the various features: Set up your allergens, add recipes to favorites, or create your shopping list.

5. Plan your meals, update your ingredients, and efficiently manage your fridge! 
