package dataaccess;

public class GameTakenException extends DataAccessException {
    public GameTakenException(String message) {
        super(message);
    }
}
