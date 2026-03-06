package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import result.RegisterRequest;
import service.RegisterService;

public class RegisterHandler {
    private RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public void register(Context ctx) {
        try {
            var req = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            var result = registerService.register(req);
            ctx.status(200);
            ctx.json(new Gson().toJson(result));
        }
        catch (BadRequestException e){
            ctx.status(400);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
        catch (AlreadyTakenException e){
            ctx.status(403);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
