package ui;

import serverfacade.ServerFacade;

import java.util.Scanner;

public class Repl {
    ServerFacade serverFacade;
    Scanner scanner;
    PreloginClient preloginClient;
    PostloginClient postloginClient;
    boolean isLoggedIn = false;

    public Repl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.preloginClient = new PreloginClient(serverFacade);
        this.postloginClient = new PostloginClient(serverFacade);
    }

    public void run() {
        System.out.println("Welcome to Chess! Type 'help' for more information.");
        String result;

        while(true) {
            if (isLoggedIn) {
                System.out.print("[LOGGED_IN] >>> ");
            }
            else {
                System.out.print("[LOGGED_OUT] >>> ");
            }

            String line = scanner.nextLine();

            if (isLoggedIn) {
                result = postloginClient.eval(line);
            }
            else {
                result = preloginClient.eval(line);
            }

            if (result.equals("quit")) {
                System.out.println("Goodbye!");
                break;
            }
            else if (result.contains("LOGIN_SUCCESS")) {
                String authToken = result.substring(result.indexOf(":") + 1);
                isLoggedIn = true;
                System.out.println("You are now logged in.");
                postloginClient.setAuthToken(authToken);
            }
            else if (result.equals("LOGOUT_SUCCESS")) {
                isLoggedIn = false;
                System.out.println("You are now logged out.");
            }
            else {
                System.out.println(result);
            }
        }
    }
}
