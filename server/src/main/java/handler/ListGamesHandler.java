package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.ListGamesRequest;
import service.ListGamesService;

public class ListGamesHandler {
    private ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var req = new ListGamesRequest(authToken);
            var result = listGamesService.listGames(req);
            ctx.status(200);
            ctx.json(new Gson().toJson(result));
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
