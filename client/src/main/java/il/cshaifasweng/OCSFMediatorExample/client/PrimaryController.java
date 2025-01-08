package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PrimaryController {
	@FXML
	private TextField text1,text2,text3,text4,text5;
	@FXML
	private Label statusLabel;

	@FXML
	private Button change1,change2,change3,change4,change5;


	@FXML
	public void initialize() {
		System.out.println("Initializing Primary Controller");
		EventBus.getDefault().register(this);
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void switchToSecondary() throws IOException {
		EventBus.getDefault().unregister(this);
		App.setRoot("secondary");
	}
	@Subscribe
	public void handleMessage(String message) {
		if (message.startsWith("added")) {
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
		if (message.startsWith("error")) {
			Platform.runLater(() -> {
				statusLabel.setText("Unexpected Error Occurred");
			});
		}
		if (message.startsWith("Menu")) {
			try {
				switchToSecondary(); // Pass the message to the second controller
			} catch (IOException e) {
				Platform.runLater(() -> {
					statusLabel.setText("שגיאה לא צפויה: " + e.getMessage());
				});
			}
		}
	}
}
