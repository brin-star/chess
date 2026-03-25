package ui;

import ServerFacade.ServerFacade;

import java.util.Scanner;

public class Repl {
    ServerFacade serverFacade;
    Scanner scanner = System.in;
    PreloginClient preloginClient(serverFacade);
    PostloginClient postloginClient(serverFacade);
    boolean isLoggedIn = false;

    public void run() {
        System.out.println("Welcome to Chess! Type 'help' for more information.");
        var result;

        while(true) {
            if (isLoggedIn) {
                System.out.println("[LOGGED_IN]");
            }
            else {
                System.out.println("[LOGGED_OUT]");
            }

            String line = scanner.nextLine();

            if (isLoggedIn) {
                result = PostloginClient.eval(line);
            }
            else {
                result = PreloginClient.eval(line);
            }

            if (result == "quit") {
                System.out.println("Goodbye!");
            }
            else if (result == "LOGIN_SUCCESS") {
                isLoggedIn = true;
            }
            else if (result == "LOGOUT_SUCCESS") {
                isLoggedIn = false;
            }
            else {
                System.out.println(result);
            }
        }
    }
}
