package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CompactMenu;
import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuController {
    @FXML
    private Button ChangePrice1;

    @FXML
    private Button ChangePrice2;

    @FXML
    private Button ChangePrice3;

    @FXML
    private Button ChangePrice4;

    @FXML
    private Label Meal1;

    @FXML
    private Label Meal2;

    @FXML
    private Label Meal3;

    @FXML
    private Label Meal4;

    @FXML
    private TextField Price1;

    @FXML
    private TextField Price2;

    @FXML
    private TextField Price3;

    @FXML
    private TextField Price4;

    private ArrayList<Label> Meals;
    private ArrayList<TextField> Prices;

    // Generalized ChangePrice function
    private void ChangePrice(int mealNumber) {
        TextField price = Prices.get(mealNumber - 1);
        try {
            int newPrice = Integer.parseInt(price.getText());
            try {
                String mealName = Meals.get(mealNumber - 1).getText().split(",")[0].split(":")[1].trim();
                SimpleClient.getClient().sendToServer("Meal name is: " + mealName + "new price is :" + newPrice);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            // Handle invalid input gracefully
            price.setText("Invalid input");
        }
    }

    // Button handlers now call the generic ChangePrice method
    @FXML
    void ChangePrice1(ActionEvent event) {
        ChangePrice(1);
    }

    @FXML
    void ChangePrice2(ActionEvent event) {
        ChangePrice(2);
    }

    @FXML
    void ChangePrice3(ActionEvent event) {
        ChangePrice(3);
    }

    @FXML
    void ChangePrice4(ActionEvent event) {
        ChangePrice(4);
    }

    @Subscribe
    public void LoadMenuList(List<Dish> menuItems) {
        System.out.println("got the Event bus for Menu");
        if (!menuItems.isEmpty()) {
            int size = Meals.size();
            Platform.runLater(() -> {
                for (int i = 0; i < size; i++) {
                    Dish dish = menuItems.get(i);

                    // Format ingredients as a comma-separated string
                    String ingredients = dish.getIngredientsString() != null && !dish.getIngredientsString().isEmpty()
                            ? Arrays.stream(dish.getIngredientsString().split(","))
                            .map(String::trim)
                            .reduce((ing1, ing2) -> ing1 + ", " + ing2)
                            .orElse("No ingredients")
                            : "No ingredients";

                    // Format preferences
                    String preferences = dish.getPreferencesString() != null
                            ? String.format(
                            "Spiciness: %s, Bread: %s",
                            dish.getPreferencesLevelOfSpiciness(),
                            dish.getPreferencesKindOfBread()
                    )
                            : "No preferences";

                    // Create a descriptive string for the meal
                    String mealInfo = String.format(
                            "Name: %s, Ingredients: %s, Preference: %s",
                            dish.getName(),
                            ingredients,
                            preferences
                    );

                    // Set label and price field
                    Meals.get(i).setText(mealInfo); // Set the detailed meal info
                    Prices.get(i).setText(String.valueOf(dish.getPrice())); // Set price
                }
            });
        } else {
            System.out.println("The menu item is empty.");
        }
    }

    @Subscribe
    public void changeMealPrice(String MealAndPrice){
        // Check if the message format is as expected
        if (MealAndPrice.startsWith("Meal name is: ")) {
            // Extract meal name and price from the message
            String mealName = MealAndPrice.split("Meal name is: ")[1].split(" and the new price is:")[0].trim();
            String newPrice = MealAndPrice.split("and the new price is: ")[1].trim();

            // Find the corresponding meal in the Meals list
            for (int i = 0; i < Meals.size(); i++) {
                // Extract the meal name from the formatted label text
                String mealLabelText = Meals.get(i).getText();
                String formattedMealName = mealLabelText.split(",")[0].split(":")[1].trim();  // Get the name part

                // Check if the extracted meal name matches the received meal name
                if (formattedMealName.equals(mealName)) {
                    // Meal found, update the label with the new price
                    Prices.get(i).setText(newPrice); // Set the new price for the corresponding meal
                     System.out.println("Updated price for " + mealName + " to " + newPrice);
                    break;  // Exit the loop after updating
                }
            }
        }
    }
    @FXML
    void initialize() {
        System.out.println("Initializing MenuController");

        Meals = new ArrayList<>();
        Meals.add(Meal1);
        Meals.add(Meal2);
        Meals.add(Meal3);
        Meals.add(Meal4);

        Prices = new ArrayList<>();
        Prices.add(Price1);
        Prices.add(Price2);
        Prices.add(Price3);
        Prices.add(Price4);

        EventBus.getDefault().register(this);

    }

    CompactMenu compactMenu;

    @FXML
    private ComboBox<String> MenuList;

    @FXML
    private Label statusLabel;

    @FXML
    void ChooseDish(ActionEvent event) {
        String choice = MenuList.getSelectionModel().getSelectedItem();
        boolean sent = false;
        while (!sent) {
            try {
                SimpleClient.getClient().sendToServer("GetDishInfo:" + choice);
                sent = true;
            } catch (Exception e) {
                // Handle exception
            }
        }
        statusLabel.setText("Loading " + choice);
        EventBus.getDefault().unregister(this);
    }
}
