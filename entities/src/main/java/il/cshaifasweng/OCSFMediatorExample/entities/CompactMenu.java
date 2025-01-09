package il.cshaifasweng.OCSFMediatorExample.entities;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompactMenu implements Serializable {
    public ArrayList<String> dishes;

    public CompactMenu(List<String> dishes) {

        //EventBus.getDefault().register(this);

        this.dishes = new ArrayList<>(dishes);
    }
}