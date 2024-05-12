package hr.java.game.monopoly.thread;
import hr.java.game.monopoly.Monopoly;
import hr.java.game.monopoly.MonopolyController;
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

            GameState.convertGameStateToGameStateWithFields(
                    gameState.getGameBoardFields(),
                    MonopolyController.boardFields
            );

            Monopoly.playerTurn = gameState.getPlayerTurn();
            MonopolyController.deactivateButtons(true);

            System.out.println("Player two received the game state!");
            oos.writeObject("Player two received the game state - confirmation!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
