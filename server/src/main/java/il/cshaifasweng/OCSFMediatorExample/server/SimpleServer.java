package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer
{
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	int count = 0;
	private int [][] board = { {0,0,0} ,{0,0,0},{0,0,0}};
	private int who_turn = 0;
	public SimpleServer(int port) {
		super(port);

	}
	private void start_game() {
		Random random = new Random();
		int randomInt = random.nextInt(2);

		String s = "the game has started";
		int the_first_player = random.nextInt(2);
		if(the_first_player == 0)
		{
			s += "the first player is player number 0";
			System.out.println(s);
			sendToAllClients(s);
		}
		else
		{
			s += "the first player is player number 1";
			System.out.println(s);
			sendToAllClients(s);
			who_turn = 1;
		}
	}
	private boolean checkVictory(int player)
	{
		for (int i = 0; i < 3; i++)
		{
			if (board[i][0] == 1 && board[i][1] == player && board[i][2] == player) return true;
			if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
		}
		if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
		if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;
		return false;
	}
	@Override
	protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		System.out.println( "SimpleServer" + " " + msgString);
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("add client"))
		{
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try
			{
				String prefix_massegae = "client added successfully,  ";
				if(SubscribersList.size() < 2)
				{
					client.sendToClient( prefix_massegae+ "but you need to wait for another player to connect, you are number 0");
				}
				else
				{
					client.sendToClient( prefix_massegae+ "there is enough players to play, you are number 1");
					start_game();
				}

			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if (msgString.startsWith("remove client"))
		{
			if (!SubscribersList.isEmpty())
			{
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client))
					{
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
		else
		{
			System.out.println("0");

			int player = 0;
			try
			{
				//checking turn
				if(!SubscribersList.get(who_turn).getClient().equals(client))
				{
					System.out.println("not your turn");
					return;
				}

				if(SubscribersList.get(1).getClient().equals(client))
				{
					player = 1;
				}

				System.out.println(msgString.charAt(0) + " "+msgString.charAt(msgString.length() - 1));
				int row = (int) msgString.charAt(0) - '0';
				int colum = (int) msgString.charAt(msgString.length() - 1) - '0';
				System.out.println("row: " + row);
				System.out.println("colum: " + colum);
				count++;
				if(player == 0)
				{
					if(board[row][colum] != 0)
						throw new RuntimeException("can't add here");
					board[row][colum] = 1;
				}
				else
				{
					if(board[row][colum] != 0)
						throw new RuntimeException("can't add here");
					board[row][colum] = -1;
				}

				System.out.println("it is turn of number" + who_turn);
				who_turn++;
				who_turn %= 2;
				SubscribersList.get(who_turn).getClient().sendToClient("the_other_player_added " + row + " " + colum);

				if(checkVictory(1) || checkVictory(2))
				{
					throw new RuntimeException("won or draw");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				sendToAllClients("the game is over");
				for(SubscribedClient subscribedClient : SubscribersList)
				{
					SubscribersList.remove(subscribedClient);
				}

			}
		}
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}

