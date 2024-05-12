package hr.java.game.monopoly.thread;
import hr.java.game.monopoly.Monopoly;
import hr.java.game.monopoly.MonopolyController;
import hr.java.game.monopoly.model.Field;
import hr.java.game.monopoly.model.GameState;
import hr.java.game.monopoly.model.PlayerTurn;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PlayerOneServerThread implements Runnable {
    @Override
    public void run() {
        playerOneAcceptRequests();
    }

    private static void playerOneAcceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(Monopoly.PLAYER_ONE_SERVER_PORT)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                Platform.runLater(() ->  processSerializableClient(clientSocket));
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState gameState = (GameState)ois.readObject();

            // Convert gameFields to string representation
            StringBuilder fieldsStringBuilder = new StringBuilder();
            for (Field field : gameState.getGameFields()) {
                fieldsStringBuilder.append(field.toString()).append(", ");
            }
            String fieldsAsString = fieldsStringBuilder.toString();
            if (fieldsAsString.length() > 2) {
                fieldsAsString = fieldsAsString.substring(0, fieldsAsString.length() - 2); // Remove trailing comma and space
            }

            System.out.println("FIELDS RECEIVED: " + fieldsAsString);

            GameState.convertGameStateToGameStateWithFields(
                    MonopolyController.boardFields,
                    gameState.getGameFields()
            );

            Monopoly.playerTurn = gameState.getPlayerTurn();
            MonopolyController.deactivateButtons(true);

            System.out.println("Player one received the game state!");
            oos.writeObject("Player two received the game state - confirmation!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
