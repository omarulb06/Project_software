package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.CompactMenu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PrimaryController {
	@FXML
	private Label statusLabel;

	@FXML
	private Button MenuBtn;


	@FXML
	public void initialize() {

		System.out.println("Initializing Primary Controller");
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void LoadMenu(ActionEvent event) {
		boolean sent = false;
		while (!sent) {
			try {
				SimpleClient.getClient().sendToServer("GetDishNames");
				sent = true;
			} catch (Exception ignored) {
			}
		}
		statusLabel.setText("Loading Menu");
	}
}