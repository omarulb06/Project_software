package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CompactMenu;
import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuUpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class SimpleClient extends AbstractClient {
	private static SimpleClient client = null;
	private SimpleClient(String host, int port) {
		super(host, port);
	}


	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof Warning) {
			EventBus.getDefault().post("ERROR");
		} else if (msg instanceof String) {
			String message = (String) msg;
			System.out.println(message);
			if (message.equals("added successfully")) {
				EventBus.getDefault().post("added");
			}
		} else if (msg instanceof CompactMenu) {
			try {
				App.setRoot("secondary");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("ABCD");
			EventBus.getDefault().post(msg);
		} else if (msg instanceof Dish) {
			try {
				App.setRoot("tertiary");
			} catch (Exception e) {
				e.printStackTrace();
			}
			EventBus.getDefault().post(msg);
		} else if (msg instanceof MenuUpdateEvent) {
			System.out.println("im here");
			try {

				App.setRoot("quaternary");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}