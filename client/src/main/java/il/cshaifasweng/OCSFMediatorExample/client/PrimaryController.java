package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import javax.swing.*;
import java.io.IOException;

public class PrimaryController {

	@FXML
	private Button button00, button01, button02, button10, button11, button12, button20, button21, button22;

	int count = 0;
	private char[][] board = new char[3][3]; // לוח המשחק
	private char mySignal; // הסימון של השחקן המקומי
	private boolean isMyTurn; // מציין אם זהו תור השחקן המקומי

	@FXML
	private Label statusLabel; // תווית להצגת סטטוס המשחק

	private boolean checkVictory(char player)
	{
		for (int i = 0; i < 3; i++)
		{
			if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
			if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
		}
		if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
		if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;
		return false;
	}

	@FXML
	public void initialize() {
		// איפוס הלוח
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = '\0'; // לוח ריק
			}
		}
		isMyTurn = false; // ברירת מחדל: לא תור השחקן
		EventBus.getDefault().register(this);
		try
		{
			SimpleClient.getClient().sendToServer("add client");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Subscribe
	public void handleMessage(String message)
	{
		System.out.println( "PrimaryController "+ message);

		if (message.startsWith("the_other_player_added"))
		{
			String[] a = message.split(" ");
			int first = Integer.parseInt(a[1]);
			int second = Integer.parseInt(a[2]);
			char c = a[3].charAt(0);
			board[first][second] = c;
			count++;

			String buttonId = "button" + first + second; // יוצר את ה-ID של הכפתור

			Platform.runLater(() -> {
				Button buttonToUpdate = (Button) statusLabel.getScene().lookup("#" + buttonId);
				if (buttonToUpdate != null) {
					buttonToUpdate.setText(String.valueOf(c)); // מעדכן את הטקסט של הכפתור
				} else {
					System.out.println("Button not found: " + buttonId);
				}
			});
			char othersignal  =  (mySignal == 'X')? 'O' : 'X';

			if(checkVictory(othersignal))
			{
				Platform.runLater(() ->statusLabel.setText("you lost"));
				return;
			}
			if(count == 9)
			{
				Platform.runLater(() ->statusLabel.setText("draw"));
				return;
			}
			isMyTurn = true;
			Platform.runLater(() ->statusLabel.setText("תורך"));
		}


		if (message.equals("the game will start soon") || message.equals("ERROR") || message.equals("wait for another player")) {
			Platform.runLater(() ->statusLabel.setText(message));
		}
		if (message.startsWith("the game started")) {
			String[] a = message.split(" ");
			mySignal = a[3].charAt(0);
			isMyTurn = Boolean.parseBoolean(a[4]);
			if(isMyTurn)
			{
				Platform.runLater(() ->statusLabel.setText("your turn"));
				return;
			}
			Platform.runLater(() ->statusLabel.setText("not your turn"));
		}

	}

	@FXML
	private void handleButtonClick(ActionEvent event) {
		if (!isMyTurn) {
			Platform.runLater(() ->statusLabel.setText("לא תורך!"));
			return;
		}

		Button clickedButton = (Button) event.getSource();
		String id = clickedButton.getId();
		int row = Integer.parseInt(id.substring(6, 7)); // שורה
		int col = Integer.parseInt(id.substring(7, 8)); // עמודה

		if (board[row][col] != '\0') {
			Platform.runLater(() ->statusLabel.setText("משבצת זו כבר תפוסה!"));
			return;
		}

		board[row][col] = mySignal;
		Platform.runLater(() ->clickedButton.setText(String.valueOf(mySignal)));

		boolean sended = false;
		while (!sended)
		{
			try {
				SimpleClient.getClient().sendToServer(row + " " + col);
				sended = true;
			} catch (IOException e) {

			}
		}

		count++;
		char otherSignal = (mySignal == 'X') ? 'O' : 'X';

		// בדיקת ניצחון ותיקו
		if (checkVictory(mySignal)) {
			Platform.runLater(() -> statusLabel.setText("ניצחת!"));
			isMyTurn = false; // מניעת המשך משחק
			return;
		}
		if (count == 9) {
			Platform.runLater(() -> statusLabel.setText("תיקו!"));
			return;
		}

		// עדכון תור
		isMyTurn = false;
		Platform.runLater(() -> statusLabel.setText("תור היריב..."));
	}


	public void startGame(char signal, boolean isFirstTurn){
		Platform.runLater(() -> {
			mySignal = signal;
			isMyTurn = isFirstTurn;
			Platform.runLater(() ->statusLabel.setText(isMyTurn ? "תורך!" : "תור היריב..."));
		});
	}

	public void updateStatus(String status) {
		Platform.runLater(() ->statusLabel.setText(status));
	}

	public void updateBoard(int row, int col, char signal) {
		Platform.runLater(() -> {
			board[row][col] = signal;
			Button targetButton = getButtonByCoordinates(row, col);
			if (targetButton != null) {
				targetButton.setText(String.valueOf(signal));
			}

			if (signal != mySignal) {
				isMyTurn = true;
				Platform.runLater(() ->statusLabel.setText("תורך!"));
			} else {
				Platform.runLater(() ->statusLabel.setText("תור היריב..."));
			}
		});
	}

	private Button getButtonByCoordinates(int row, int col) {
		switch (row * 3 + col) {
			case 0: return button00;
			case 1: return button01;
			case 2: return button02;
			case 3: return button10;
			case 4: return button11;
			case 5: return button12;
			case 6: return button20;
			case 7: return button21;
			case 8: return button22;
			default: return null;
		}
	}
}
