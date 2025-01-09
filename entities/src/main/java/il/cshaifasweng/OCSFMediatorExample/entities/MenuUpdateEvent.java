
package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class MenuUpdateEvent implements Serializable {
    public Dish dish;
    public MenuUpdateEvent(Dish dish) {
        this.dish = dish;
    }
}
