package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CompactMenu;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {
    @FXML
    public void initialize() {
        System.out.println("Initializing Secondary Controller");
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
            }
        }
        statusLabel.setText("loading " + choice);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object event) {
        if (event instanceof CompactMenu) {
            compactMenu = (CompactMenu) event;
            var list = FXCollections.observableList(compactMenu.dishes);
            System.out.println("the length of list is :" + list.size());
            MenuList.setItems(list);
        }
    }
}