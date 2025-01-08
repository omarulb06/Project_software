package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {
    @FXML
    public void initialize() {
        System.out.println("Initializing Secondary Controller");
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void handleMessage(String message) {
        if (message.startsWith("added"))
        {
            String[] a = message.split(" ");
            int id = Integer.parseInt(a[1]);

            String buttonId = "change" + id; // יוצר את ה-ID של הכפתור

            Platform.runLater(() -> {
                Button buttonToUpdate = (Button) statusLabel.getScene().lookup("#" + buttonId);
                if (buttonToUpdate != null) {
                    statusLabel.setText(" עודכן בהצלחה עבור מנה "+id);
                }
                else {
                    statusLabel.setText(" שגיאה עבור מנה "+id);
                }
            });
        }
        if (message.startsWith("error"))
        {

            Platform.runLater(() -> {
                statusLabel.setText("שגיאה לא צפויה");
            });
        }
        if (message.startsWith("Menu")) {
            try {
                switchToSecondary(message); // Pass the message to the second controller
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("שגיאה לא צפויה: " + e.getMessage());
                });
            }
        }
    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}