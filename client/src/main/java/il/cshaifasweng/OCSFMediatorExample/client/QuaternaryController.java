package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class QuaternaryController {

    @FXML
    private Button BackBtn;

    @FXML
    void switchToPrimary(ActionEvent event) {
        try {
            SimpleClient.getClient().sendToServer("remove client");
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}