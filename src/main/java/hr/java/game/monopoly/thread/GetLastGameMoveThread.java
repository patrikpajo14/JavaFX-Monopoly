package hr.java.game.monopoly.thread;

import hr.java.game.monopoly.MonopolyController;
import hr.java.game.monopoly.model.GameMove;
import hr.java.game.monopoly.model.Player;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {

    private Label label;
    private AnchorPane[] boardFields;

    public GetLastGameMoveThread(Label label, AnchorPane[] boardFields) {
        this.label = label;
        this.boardFields = boardFields;
    }

    @Override
    public void run() {
        GameMove lastGameMove = getLastGameMoveFromFile();

        label.setText("Last game move: "
                + lastGameMove.getPlayerTurn().name() + "; "
                + lastGameMove.getNewPosition() + ", "
                + lastGameMove.getLocalDateTime());

        managePlayerLabel(lastGameMove.getOldPosition(), false, lastGameMove.getPlayerTurn().name());
        managePlayerLabel(lastGameMove.getNewPosition(), true, lastGameMove.getPlayerTurn().name());
    }

    public void managePlayerLabel(Integer position, Boolean state, String playerName) {
        for (Node node : boardFields[position].getChildren()) {
            System.out.println("Node: " + node);
            if (node instanceof Label) {
                Label playerLabel = (Label) node;
                if(playerName.equals(playerLabel.getId())) {
                    System.out.println("-------------- player.getName() == playerLabel.getId(): " + playerName.equals(playerLabel.getId()) + " " + playerLabel.getId() + " " + playerName);
                    playerLabel.setVisible(state);
                }
            }
        }
    }
}
