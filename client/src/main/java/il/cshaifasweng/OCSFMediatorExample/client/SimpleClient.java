package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.ArrayList;

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
		} else if (msg instanceof Menu) {
			EventBus.getDefault().post((Menu) msg);
		}
	}

	public static synchronized SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}
