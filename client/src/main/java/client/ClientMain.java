package client;

import ServerFacade.ServerFacade;
import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade serverFacade = new ServerFacade(8080);
        Repl repl = new Repl(serverFacade);
        repl.run();
    }
}
