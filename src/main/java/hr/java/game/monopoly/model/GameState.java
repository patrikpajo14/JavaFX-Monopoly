package hr.java.game.monopoly.model;

import hr.java.game.monopoly.MonopolyController;
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

    private Field[] gameBoardFields;
    private Integer diceNumber;
    private Player playerOne;
    private Player playerTwo;
    private Integer playerOnePosition;
    private Integer playerTwoPosition;
    private PlayerTurn playerTurn;

    public static String[] convertGameStateWithFieldsToGameStateString(AnchorPane[] gameStateWithAnchors)
    {
        String[] gameStateWithString = new String[NUMBER_OF_ROWS * NUMBER_OF_COLUMNS];

        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            gameStateWithString[i] = gameStateWithAnchors[i].toString();
        }

        return gameStateWithString;
    }

    public static void convertGameStateToGameStateWithFields(Field[] gameBoardFields,  Field[] gameStateFields )
    {
        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            gameBoardFields[i] = gameStateFields[i];
        }
    }
}
