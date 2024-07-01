package hr.java.game.monopoly.util;

import hr.java.game.monopoly.model.GameMove;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameMoveUtils {

    private static final String GAME_MOVE_HISTORY_FILE_NAME = "gameMoves/gameMoves.dat";
    private static List<GameMove> gameMoveList = new ArrayList<>();

    public static void saveNewGameMove(GameMove newGameMove) {
        gameMoveList.add(newGameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(GAME_MOVE_HISTORY_FILE_NAME)))
        {
            oos.writeObject(gameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameMove getLastGameMove() {

        //List<GameMove> gameMoves;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(GAME_MOVE_HISTORY_FILE_NAME)))
        {
            gameMoveList = (List<GameMove>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(!gameMoveList.isEmpty()){
            return gameMoveList.getLast();
        }else{
            return null;
        }
    }


    public static void initializeGame() {
        gameMoveList.clear();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(GAME_MOVE_HISTORY_FILE_NAME)))
        {
            oos.writeObject(gameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
