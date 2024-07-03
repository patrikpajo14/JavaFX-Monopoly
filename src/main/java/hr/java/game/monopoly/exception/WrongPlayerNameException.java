package hr.java.game.monopoly.exception;

public class WrongPlayerNameException extends RuntimeException {
    public WrongPlayerNameException(String message) {
        super(message);
    }
}
