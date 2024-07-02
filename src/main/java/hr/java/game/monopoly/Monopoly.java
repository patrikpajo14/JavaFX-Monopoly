package hr.java.game.monopoly;

import hr.java.game.monopoly.exception.WrongPlayerNameException;
import hr.java.game.monopoly.jndi.ConfigurationReader;
import hr.java.game.monopoly.model.Player;
import hr.java.game.monopoly.model.PlayerTurn;
import hr.java.game.monopoly.thread.PlayerOneServerThread;
import hr.java.game.monopoly.thread.PlayerTwoServerThread;
import hr.java.game.monopoly.util.GameMoveUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class Monopoly extends Application {

    public static PlayerTurn playerTurn;

    public static final int PLAYER_TWO_SERVER_PORT = 1989;
    public static final int PLAYER_ONE_SERVER_PORT = 1990;

    public static final String HOST = "localhost";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Monopoly.class.getResource("monopoly-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 950, 780);
        stage.setTitle(playerTurn.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        GameMoveUtils.initializeGame();

        String firstArgument = args[0];

        if(PlayerTurn.valueOf(firstArgument).equals(PlayerTurn.PLAYER_ONE)) {
            playerTurn = PlayerTurn.PLAYER_ONE;
            Thread serverStater = new Thread(new PlayerOneServerThread());
            serverStater.start();
        }
        else if (PlayerTurn.valueOf(firstArgument).equals(PlayerTurn.PLAYER_TWO)) {
            playerTurn = PlayerTurn.PLAYER_TWO;
            Thread serverStater = new Thread(new PlayerTwoServerThread());
            serverStater.start();
        }
        else if (PlayerTurn.valueOf(firstArgument).equals(PlayerTurn.SINGLE_PLAYER)) {
            playerTurn = PlayerTurn.SINGLE_PLAYER;
        }
        else {
            throw new WrongPlayerNameException("The game was started with the player name: "
                    + firstArgument + ", but only PLAYER_ONE and PLAYER_TWO are supported.");
        }

        launch();
    }
}