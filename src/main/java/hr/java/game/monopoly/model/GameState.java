package hr.java.game.monopoly.model;

import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState implements Serializable {

    public static final String SAVE_GAME_FILE_NAME = "saveGame/gameSave.dat";

    public static final Integer NUMBER_OF_ROWS = 4;
    public static final Integer NUMBER_OF_COLUMNS = 4;

    private String[] gameBoardState;
    private Integer numberOfMoves;
    private Player turn;

    public static String[] convertGameStateWithFieldsToGameStateString(AnchorPane[] gameStateWithAnchors)
    {
        String[] gameStateWithString = new String[NUMBER_OF_ROWS * NUMBER_OF_COLUMNS];

        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            gameStateWithString[i] = gameStateWithAnchors[i].getId();
        }

        return gameStateWithString;
    }

    public static void convertGameStateWithStringToGameStateWithFields(
            String[] gameStateWithStrings, AnchorPane[] gameStateWithAnchors)
    {
        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            gameStateWithAnchors[i].setId(gameStateWithStrings[i]);
        }
    }
}
