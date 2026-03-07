package handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.LoginRequest;
import service.LoginService;

public class LoginHandler {
    private LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login(Context ctx) {
        try {
            var req = new Gson().fromJson(ctx.body(), LoginRequest.class);
            var result = loginService.login(req);
            ctx.status(200);
            ctx.json(new Gson().toJson(result));
        }
        catch (BadRequestException e) {
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
