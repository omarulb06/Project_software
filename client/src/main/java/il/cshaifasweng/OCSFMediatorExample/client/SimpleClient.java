package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CompactMenu;
import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuUpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.util.List;
import java.io.IOException;

public class SimpleClient extends AbstractClient {
    private static SimpleClient client = null;

    private SimpleClient(String host, int port) {
        super(host, port);
    }


    @Override
    protected void handleMessageFromServer(Object msg) {
        System.out.println("Client in handleMessageFromServer");
        if(msg instanceof String){
            if (msg.toString().equals("client added successfully")) {
                System.out.println("hello");
                return ;
            }
            else if (msg.toString().startsWith("Meal")) {
                System.out.println("im in msg.toString().startsWith(Meal)");
                Platform.runLater(() -> {
                    try {
                        App.setRoot("MenuController");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            else if (msg.toString().startsWith("Success")) {
                System.out.println("im in message.startsWith(Success)");
                String mealName = msg.toString().split("The price for")[1].split("was updated to")[0].trim();
                String newPrice = msg.toString().split("was updated to")[1].replace(".", "").trim();
                EventBus.getDefault().post("Meal name is: " + mealName+" and the new price is: " + newPrice);
            }
        }
        else if(msg instanceof List) {
            System.out.println("Client got the menu");
            List<?> rawList = (List<?>) msg; // Cast msg to a raw List
            if (rawList.isEmpty() || rawList.get(0) instanceof Dish) {
                @SuppressWarnings("unchecked") // Suppress unchecked cast warning
                List<Dish> menuItems = (List<Dish>) rawList;
                Platform.runLater(() -> {
                    try {
                        System.out.println("trying to set root for menu");
                        App.setRoot("Menu");
                        System.out.println("finishing to set root for menu");
                        System.out.println("Event bus to menu controller");
                        EventBus.getDefault().post(menuItems);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } else {
                System.out.println("The list is not a List<Dish>.");
            }

        }

	}

    public static synchronized SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient("127.0.0.1", 3000);
        }
        return client;
    }
}