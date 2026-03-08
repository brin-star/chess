package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.LogoutRequest;
import service.LogoutService;

public class LogoutHandler {
    private LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var req = new LogoutRequest(authToken);
            var result = logoutService.logout(req);
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
