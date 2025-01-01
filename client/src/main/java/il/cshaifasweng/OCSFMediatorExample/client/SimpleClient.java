package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;

public class SimpleClient extends AbstractClient {

	private int player_number = 0;
	private static SimpleClient client = null;
	char signal;
	boolean isFirstTurn = false;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg)
	{
		if (msg instanceof Warning) {
			EventBus.getDefault().post("ERROR");
		}
		else if (msg instanceof String) {
			System.out.println((String) msg);
			String message = (String) msg;
            if(message.startsWith("client added successfully,"))
			{
				if(!message.contains("but"))
				{
					EventBus.getDefault().post("the game will start soon");
					player_number++;
				}
				else
				{
					EventBus.getDefault().post("wait for another player");
				}
			}
			if (message.startsWith("the game has started"))
			{
				if( (message.contains("0") && player_number == 0) || (message.contains("1") && player_number == 1) )
				{
					signal = 'X';
					isFirstTurn = true;
				}
				else
				{
					signal = 'O';
				}
				EventBus.getDefault().post("the game started " + signal + " " + isFirstTurn);
			}
			else if (message.startsWith("the_other_player_added")) {
				String[] parts = message.split(" ");
				int row = Integer.parseInt(parts[1]);
				int col = Integer.parseInt(parts[2]);
				char c = (signal == 'X') ? 'O' : 'X';
				EventBus.getDefault().post("the_other_player_added " + row +" "+ col +" "+ c);
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
