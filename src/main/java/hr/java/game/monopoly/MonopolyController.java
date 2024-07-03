package hr.java.game.monopoly;

import hr.java.game.monopoly.chat.ChatService;
import hr.java.game.monopoly.jndi.ConfigurationReader;
import hr.java.game.monopoly.model.*;
import hr.java.game.monopoly.thread.GetLastGameMoveThread;
import hr.java.game.monopoly.thread.SaveNewGameMoveThread;
import hr.java.game.monopoly.util.DialogUtils;
import hr.java.game.monopoly.util.DocumentationUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Label lastGameMoveLabel;
    @FXML
    private ImageView diceImage;
    @FXML
    private Button rollButton;
    @FXML
    private Button bankruptButton;
    @FXML
    private Button sendMessageButton;
    @FXML
    private TextField chatInput;
    @FXML
    private TextArea chatTextArea;
    private static ChatService stub;

    private Dice dice;
    public static AnchorPane[] boardState;
    public static Field[] boardFields;

    public static Player player1;
    public static Player player2;

    List<GameMove> gameMoves = new ArrayList<>();

    @FXML
    public void initialize() {
        dice = new Dice(diceImage, 1);
        if(player_name != null) player_name.setText(Monopoly.playerTurn.name());
        player1 = new Player(1, PlayerTurn.PLAYER_ONE.name(), 2000);
        player2 = new Player(2, PlayerTurn.PLAYER_TWO.name(), 2000);

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
                    String labelText = "";
                    if(label.getId().equals("field_name")){
                        labelText = label.getText();
                        if(i != 0 && i != 4 && i != 8 && i != 12){
                            boardFields[i] = new Field(i, labelText, 500 + i * 15, 150 + i * 15);
                        }else {
                            boardFields[i] = new Field(i, labelText, 0, 0);
                        }
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

        if(!Monopoly.playerTurn.name().equals(PlayerTurn.SINGLE_PLAYER.name())) {
            try {
                String rmiPort = ConfigurationReader.getValue(ConfigurationKey.RMI_PORT);
                String serverName = ConfigurationReader.getValue(ConfigurationKey.RMI_HOST);
                Registry registry = LocateRegistry.getRegistry(serverName, Integer.parseInt(rmiPort));
                stub = (ChatService) registry.lookup(ChatService.REMOTE_OBJECT_NAME);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                refreshChatTextArea();
                if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
                    fillPlayerInfo(player1);
                } else {
                    fillPlayerInfo(player2);
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            Platform.runLater(new GetLastGameMoveThread(lastGameMoveLabel, boardState));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        fillInfoLog("Your turn, roll dice!");
    }

    private void refreshChatTextArea() {
        List<String> chatHistory = null;
        try {
            chatHistory = stub.returnChatHistory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();

        for (String message : chatHistory) {
            sb.append(message);
            sb.append("\n");
        }

        chatTextArea.setText(sb.toString());
        chatTextArea.setScrollTop(Double.MAX_VALUE);
    }

    public void sendChatMessage() {
        String chatMessage = chatInput.getText();
        String playerName = Monopoly.playerTurn.name();

        try {
            stub.sendChatMessage(playerName + ": " + chatMessage);
            refreshChatTextArea();
            chatInput.setText("");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
        rollButton.setDisable(true);
        Platform.runLater(() -> {
            GameMove newGameMove = new GameMove();
            newGameMove.setPlayerTurn(Monopoly.playerTurn);
            newGameMove.setOldPosition(player.getCurrentField());
            newGameMove.setLocalDateTime(LocalDateTime.now());

            if (player.getCurrentField() + diceNumber >= boardState.length){
                int tmpBr = player.getCurrentField() + diceNumber - boardState.length;
                managePlayerLabel(player, false);
                player.setCurrentField(tmpBr);
                managePlayerLabel(player, true);
                newGameMove.setNewPosition(player.getCurrentField());
            }else{
                managePlayerLabel(player, false);
                player.setCurrentField(player.getCurrentField() + diceNumber);
                managePlayerLabel(player, true);
                newGameMove.setNewPosition(player.getCurrentField());
            }
            checkIfFieldIsPurchased(player, boardFields[player.getCurrentField()]);

            gameMoves.add(newGameMove);

            SaveNewGameMoveThread saveNewGameMoveThread = new SaveNewGameMoveThread(newGameMove);
            Thread starter = new Thread(saveNewGameMoveThread);
            starter.start();
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
        } else {
            System.out.println("FILED OWNER: " + field.getOwner().getName());
            if (field.getOwner().getId() == player.getId()) {
                if (player.getWallet() > field.getPrice() * 15 / 100) {
                    fillInfoLog("You moved to " + field.getTitle() + ". Do you want to upgrade your field for " + field.getPrice() * 15 / 100 + "€?");
                    buyButton.setDisable(false);
                    payRentButton.setDisable(true);
                } else {
                    fillInfoLog("You moved to " + field.getTitle() + ". You don't have money to upgrade your field for " + field.getPrice() * 15 / 100 + "€. please click next.");
                    buyButton.setDisable(true);
                    payRentButton.setDisable(true);
                }
            }else{
                buyButton.setDisable(true);
                payRentButton.setDisable(false);
                fillInfoLog("You moved to "+ field.getOwner().getName() + " field " + field.getTitle() + ". You need to pay a rent of " + field.getRentPrice() + "€!");
            }
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

    void payRentAction(Player player, Field field) {
        if (field.getOwner() != null && field.getOwner().getId() != player.getId()) {
            int rentAmount = field.getRentPrice();
            player.payRent(player.getId() == player1.getId() ? player2 : player1, rentAmount);
            fillPlayerInfo(player);
            fillInfoLog(player.getName() + " paid " + rentAmount + "€ rent to " + field.getOwner().getName() + ".");
        }
    }

    public static void managePlayerLabel(Player player, Boolean state) {
        for(int i = 0; i < boardState.length; i++ ){
            for (Node node : boardState[i].getChildren()) {
                if (node instanceof Label) {
                    Label playerLabel = (Label) node;
                    if(player.getName().equals(playerLabel.getId())) {
                        if(player.getCurrentField() == i){
                            playerLabel.setVisible(state);
                        }else{
                            playerLabel.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    @FXML
    void roll(ActionEvent event) {

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
                        fillPlayerInfo(player1);
                    }else{
                        movePlayer(player2, player2Label, dice.getDiceNumber());
                        fillPlayerInfo(player2);
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
        Thread thread = new Thread() {
            public void run() {
                try {
                    if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
                        payRentAction(player1, boardFields[player1.getCurrentField()]);
                    } else {
                        payRentAction(player2, boardFields[player2.getCurrentField()]);
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
            if(player1.getWallet() == 0){
                DialogUtils.showWinnerDialog(player2);
            }
        } else {
            if(player2.getWallet() == 0){
                DialogUtils.showWinnerDialog(player1);
            }
        }
    }

    @FXML
    void goBankrupt(ActionEvent event) {
        if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
            player1.setWallet(0);
            fillInfoLog("Player " + player1.getName() + " went bankrupt!");
            DialogUtils.showWinnerDialog(player2);
        } else {
            player2.setWallet(0);
            fillInfoLog("Player " + player2.getName() + " went bankrupt!");
            DialogUtils.showWinnerDialog(player1);
        }
        nextButton.setDisable(true);
        payRentButton.setDisable(true);
        buyButton.setDisable(true);
        rollButton.setDisable(true);
        bankruptButton.setDisable(true);
    }

    @FXML
    void next(ActionEvent event) {
        nextButton.setDisable(true);
        GameState gameState = new GameState();
        gameState.setGameFields(boardFields);


        if(Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())){
            gameState.setPlayerTurn(PlayerTurn.valueOf(player2.getName()));
        }else{
            gameState.setPlayerTurn(PlayerTurn.valueOf(player1.getName()));
        }
        gameState.setPlayerOne(player1);
        gameState.setPlayerTwo(player2);
        gameState.setPlayerOnePosition(player1.getCurrentField());
        gameState.setPlayerTwoPosition(player2.getCurrentField());

        if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_ONE.name())) {
            playerOneSendRequest(gameState);
        } else if (Monopoly.playerTurn.name().equals(PlayerTurn.PLAYER_TWO.name())) {
            playerTwoSendRequest(gameState);
        }
        buyButton.setDisable(true);
        rollButton.setDisable(false);

        fillInfoLog("Your turn, roll dice!");
    }

    private static void playerOneSendRequest(GameState gameState) {
        try (Socket clientSocket = new Socket(Monopoly.HOST, Monopoly.PLAYER_TWO_SERVER_PORT)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequestToPlayerTwo(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void playerTwoSendRequest(GameState gameState) {
        try (Socket clientSocket = new Socket(Monopoly.HOST, Monopoly.PLAYER_ONE_SERVER_PORT)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequestToPlayerOne(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequestToPlayerTwo(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        Platform.runLater(() -> {
            player1.setWallet(gameState.getPlayerOne().getWallet());
            player2.setWallet(gameState.getPlayerTwo().getWallet());
        });
    }

    private static void sendSerializableRequestToPlayerOne(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        Platform.runLater(() -> {
            player1.setWallet(gameState.getPlayerOne().getWallet());
            player2.setWallet(gameState.getPlayerTwo().getWallet());
        });
    }

    public void generateHtmlDocumentation() {
        DocumentationUtils.generateDocumentation();
    }

}