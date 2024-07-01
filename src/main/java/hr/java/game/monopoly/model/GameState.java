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

    private Field[] gameFields;
    private Integer diceNumber;
    private Player playerOne;
    private Player playerTwo;
    private Integer playerOnePosition;
    private Integer playerTwoPosition;
    private PlayerTurn playerTurn;

    public static String[] convertGameStateWithFieldsToGameStateString(Field[] gameStateWithFields)
    {
        String[] gameStateWithString = new String[NUMBER_OF_ROWS * NUMBER_OF_COLUMNS];

        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            gameStateWithString[i] = gameStateWithFields[i].toString();
        }

        return gameStateWithString;
    }



    public static void convertGameStateToGameStateWithFields(Field[] gameBoardFields,  Field[] gameStateFields )
    {
        System.out.println("FIELDS TO STRING: " + gameBoardFields.toString());
        for(int i = 0; i < NUMBER_OF_COLUMNS * NUMBER_OF_ROWS; i++) {
            System.out.println("FIELD " + i + ": " + gameBoardFields[i].getTitle());

            if(gameBoardFields[i].getOwner() != null) {
                System.out.println("FIELD " + i + " owner: " + gameBoardFields[i].getOwner().getName());
            }else{
                System.out.println("FIELD " + i + " owner: null");
            }
            gameBoardFields[i] = gameStateFields[i];
        }
    }
}
