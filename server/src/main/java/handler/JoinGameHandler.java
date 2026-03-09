package handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.GameTakenException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.JoinGameRequest;
import service.JoinGameService;

public class JoinGameHandler {
    private JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public void updateGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var body = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
            var req = new JoinGameRequest(authToken, body.gameID(), body.playerColor());
            var result = joinGameService.updateGame(req);
            ctx.status(200);
            ctx.json(new Gson().toJson(result));
        }
        catch (BadRequestException e){
            ctx.status(400);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
        catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
        catch (GameTakenException e) {
            ctx.status(403);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
