package result;

import chess.ChessGame;

public record JoinGameRequest(String authToken, int gameID, ChessGame.TeamColor playerColor) {
}
