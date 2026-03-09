package handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.CreateGameRequest;
import service.CreateGameService;

public class CreateGameHandler {
    private CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var body = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
            var req = new CreateGameRequest(authToken, body.gameName());
            var result = createGameService.createGame(req);
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
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
