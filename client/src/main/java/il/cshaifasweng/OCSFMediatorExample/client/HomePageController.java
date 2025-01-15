package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class HomePageController {
    @FXML
    private Label statusLabel;

    @FXML
    private Button MenuBtn;

    @FXML
    private Button Complaintbtn;

    @FXML
    private Button Log_inBtn;

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
    void LoadMenu(ActionEvent event) throws IOException {
        try {
            SimpleClient.getClient().sendToServer("send menu");
        } catch (Exception e) {
			e.printStackTrace();
        }

    }

    @FXML
    void LoadComplaint(ActionEvent event) {

    }

    @FXML
    void LoadLogin(ActionEvent event) {

    }
}