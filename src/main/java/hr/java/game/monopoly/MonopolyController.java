package hr.java.game.monopoly;

import hr.java.game.monopoly.model.*;
import hr.java.game.monopoly.util.DocumentationUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class MonopolyController {

    @FXML
    private Text player_name;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private AnchorPane anchorPane00;
    @FXML
    private AnchorPane anchorPane01;
    @FXML
    private AnchorPane anchorPane02;
    @FXML
    private AnchorPane anchorPane03;
    @FXML
    private AnchorPane anchorPane04;
    @FXML
    private AnchorPane anchorPane14;
    @FXML
    private AnchorPane anchorPane24;
    @FXML
    private AnchorPane anchorPane34;
    @FXML
    private AnchorPane anchorPane44;
    @FXML
    private AnchorPane anchorPane43;
    @FXML
    private AnchorPane anchorPane42;
    @FXML
    private AnchorPane anchorPane41;
    @FXML
    private AnchorPane anchorPane40;
    @FXML
    private AnchorPane anchorPane30;
    @FXML
    private AnchorPane anchorPane20;
    @FXML
    private AnchorPane anchorPane10;
    @FXML
    private Button nextButton;
    @FXML
    private Button buyButton;
    @FXML
    private Button payRentButton;
    @FXML
    private TextArea playerInfoTextArea;
    @FXML
    private TextArea infoLogTextArea;
    @FXML
    private ImageView diceImage;
    @FXML
    private Button rollButton;
    private Dice dice;
    public static AnchorPane[] boardState;
    public static Field[] boardFields;

    public static Player player1;
    public static Player player2;
    @FXML
    public void initialize() {
        dice = new Dice(diceImage, 1);
        if(player_name != null) player_name.setText(Monopoly.playerTurn.name());
        player1 = new Player(1, player1Label, PlayerTurn.PLAYER_ONE.name(), 5000);
        player2 = new Player(2, player2Label, PlayerTurn.PLAYER_TWO.name(), 5000);

        boardState = new AnchorPane[GameState.NUMBER_OF_ROWS * GameState.NUMBER_OF_COLUMNS];

        boardState[0] = anchorPane00;
        boardState[1] = anchorPane01;
        boardState[2] = anchorPane02;
        boardState[3] = anchorPane03;
        boardState[4] = anchorPane04;
        boardState[5] = anchorPane14;
        boardState[6] = anchorPane24;
        boardState[7] = anchorPane34;
        boardState[8] = anchorPane44;
        boardState[9] = anchorPane43;
        boardState[10] = anchorPane42;
        boardState[11] = anchorPane41;
        boardState[12] = anchorPane40;
        boardState[13] = anchorPane30;
        boardState[14] = anchorPane20;
        boardState[15] = anchorPane10;

        boardFields = new Field[GameState.NUMBER_OF_ROWS * GameState.NUMBER_OF_COLUMNS];

        for (int i = 0; i < boardState.length; i++) {
            for (Node node : boardState[i].getChildren()) {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    String labelText = label.getText();
                    if(i != 0 && i != 4 && i != 8 && i != 12){
                        boardFields[i] = new Field(i, labelText, 500 + i * 15, 150 + i * 15);
                    }else {
                        boardFields[i] = new Field(i, labelText, 0, 0);
                    }
                }
            }
        }

        if(Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())){
            fillPlayerInfo(player1);
        }else{
            fillPlayerInfo(player2);
        }

        buyButton.setDisable(true);
        payRentButton.setDisable(true);
        nextButton.setDisable(true);

        fillInfoLog("Your turn, roll dice!");
    }

    void fillPlayerInfo (Player player){
        StringBuilder sb = new StringBuilder();
        sb.append("Wallet balance: ").append(player.getWallet()).append("€.");
        sb.append("\n");
        sb.append("Field owns: ");
        sb.append("\n");
        for (int i = 0; i < player.getFieldsOwns().toArray().length; i++){
            sb.append(player.getFieldsOwns().get(i).getTitle()).append(" (rent: ").append(player.getFieldsOwns().get(i).getRentPrice()).append("€)\n");
        }
        playerInfoTextArea.setText(sb.toString());
        playerInfoTextArea.setEditable(false);
    }

    void fillInfoLog (String log){
        infoLogTextArea.setText(log);
        infoLogTextArea.setEditable(false);
    }

    void movePlayer (Player player, Label playerLabel, int diceNumber){
        Platform.runLater(() -> {
            if (player.getCurrentField() + diceNumber >= boardState.length){
                int tmpBr = player.getCurrentField() + diceNumber - boardState.length;
                boardState[player.getCurrentField()].getChildren().remove(playerLabel);
                boardState[tmpBr].getChildren().add(playerLabel);
                player.setCurrentField(tmpBr);
            }else{
                boardState[player.getCurrentField()].getChildren().remove(playerLabel);
                boardState[player.getCurrentField() + diceNumber].getChildren().add(playerLabel);
                player.setCurrentField(player.getCurrentField() + diceNumber);
            }
            checkIfFieldIsPurchased(player, boardFields[player.getCurrentField()]);
        });
    }

    void checkIfFieldIsPurchased (Player player, Field field){
        if(field.getOwner() == null){
            if(field.getPrice() == 0){
                fillInfoLog("You moved to " + field.getTitle() + "." );
                buyButton.setDisable(true);
                payRentButton.setDisable(true);
            }else{
                if(player.getWallet() > field.getPrice()){
                    fillInfoLog("You moved to " + field.getTitle() + ". This field costs " + field.getPrice() + "€. Do you want to buy this field or play on?" );
                    buyButton.setDisable(false);
                    payRentButton.setDisable(true);
                }else{
                    fillInfoLog("You moved to " + field.getTitle() + ". This field costs " + field.getPrice() + "€. You don't have money to buy this field, please click next." );
                    buyButton.setDisable(true);
                    payRentButton.setDisable(true);
                }
            }
        } else if (field.getOwner().getId() == player.getId()) {
            if(player.getWallet() > field.getPrice() * 15 / 100){
                fillInfoLog("You moved to " + field.getTitle() + ". Do you want to upgrade your field for " + field.getPrice() * 15 / 100 + "€?");
                buyButton.setDisable(false);
                payRentButton.setDisable(true);
            }else{
                fillInfoLog("You moved to " + field.getTitle() + ". You don't have money to upgrade your field for " + field.getPrice() * 15 / 100 + "€. please click next.");
                buyButton.setDisable(true);
                payRentButton.setDisable(true);
            }
        } else{
            buyButton.setDisable(true);
            payRentButton.setDisable(false);
            fillInfoLog("You moved to "+ player.getName()+ " field " + field.getTitle() + ". You need to pay a rent of " + field.getRentPrice() + "€!");
        }
    }

    void buyField (Player player, Field field){
        if(field.getOwner() == null){
            player.buyField(field.getPrice(), field);
            field.setOwner(player);
        } else{
            if(field.getOwner().getId() == player.getId()){
                player.buyField(field.getPrice() * 15 / 100, field);
                field.setRentPrice(field.getRentPrice() + field.getRentPrice() * 15 / 100);
            }
        }
        fillPlayerInfo(player);
    }

    @FXML
    void roll(ActionEvent event) {

        rollButton.setDisable(true);

        Thread thread = new Thread(){
            public void run(){
                try {
                    for (int i = 0; i < 15; i++) {
                        dice.roll();
                        Thread.sleep(50);
//                        rollButton.setDisable(false);
                        buyButton.setDisable(false);
                        payRentButton.setDisable(false);
                        nextButton.setDisable(false);
                    }
                    if(Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())){
                        movePlayer(player1, player1Label, dice.getDiceNumber());
                    }else{
                        movePlayer(player2, player2Label, dice.getDiceNumber());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        fillInfoLog("Dice is rolling...");
    }

    @FXML
    void buy(ActionEvent event) {
        buyButton.setDisable(true);
        Thread thread = new Thread(){
            public void run(){
                try {
                    if(Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())){
                        buyField(player1, boardFields[player1.getCurrentField()]);
                    }else{
                        buyField(player2, boardFields[player2.getCurrentField()]);
                    }
                    Thread.sleep(50);
                    fillInfoLog("You bought this field. Please click next button.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();
    }

    @FXML
    void payRent(ActionEvent event) {
        payRentButton.setDisable(true);
        Thread thread = new Thread(){
            public void run(){
                try {
                    System.out.println(Monopoly.playerTurn.name() + " pressed Pay rent button");
                    Thread.sleep(50);
                    payRentButton.setDisable(false);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();
    }

    @FXML
    void next(ActionEvent event) {
        nextButton.setDisable(true);
        GameState gameState = new GameState();
        gameState.setGameBoardState(new String[GameState.NUMBER_OF_ROWS * GameState.NUMBER_OF_COLUMNS]);

        for (int i = 0; i < GameState.NUMBER_OF_ROWS * GameState.NUMBER_OF_COLUMNS; i++) {
            gameState.getGameBoardState()[i] = boardFields[i].getTitle();
        }

        if(Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())){
            gameState.setTurn(player2);
        }else{
            gameState.setTurn(player1);
        }


        Thread thread = new Thread(){
            public void run(){
                try {
                    if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
                        playerOneSendRequest(gameState);
                    } else if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_TWO.name())) {
                        playerTwoSendRequest(gameState);
                    }
                    Thread.sleep(50);
                    nextButton.setDisable(false);
                    rollButton.setDisable(false);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        fillInfoLog("Your turn, roll dice!");
    }

    private static void playerOneSendRequest(GameState gameState) {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(Monopoly.HOST, Monopoly.PLAYER_TWO_SERVER_PORT)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequestToPlayerTwo(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void playerTwoSendRequest(GameState gameState) {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(Monopoly.HOST, Monopoly.PLAYER_ONE_SERVER_PORT)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequestToPlayerOne(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequestToPlayerTwo(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to Player two!");
    }

    private static void sendSerializableRequestToPlayerOne(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to Player one!");
    }

    public void generateHtmlDocumentation() {
        DocumentationUtils.generateDocumentation();
        System.out.println("DocumentationUtils");
    }

}