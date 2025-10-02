package main;

import java.io.IOException;
import java.util.Scanner;

import app.guimanagers.AuthManager;
import app.loaders.LoadCSV;
import app.loaders.ProjectsDB;
import gui.Terminal;
import gui.UserInterface;
import models.User;

public class MainMenu {
    public static String[] options;

    public static String errorMsg = "";
    public static Integer menuID = 1;
    private static String input = "1";
    public static Integer width = 80;
    private static UserInterface ui;

    public static Boolean loggedIn = false;
    public static User activeUser;

    public static void main(String[] args) throws IOException {

        width = getTerminalWidth(args.length > 0 ? args[0] : null) - 3;
        UserInterface ui = UserInterface.getInstance(width);
        Scanner scanner = new Scanner(System.in);
        preloadData();

        // ------ render loop --------
        while (true) {
            AuthManager.initialize(width, ui);
            AuthManager.applyMenu();
            ProjectsDB.resetProjects();

            while (!loggedIn) {
                Terminal.clearScreen();
                printErrorMsg();
                ui.freshUI(input);

                System.out.print("INPUT: ");
                String input = scanner.nextLine();

                AuthManager.processCommand(input);
                AuthManager.applyMenu();
            }

            activeUser.getStrategy().initialize(width, ui);
            input = "1";
            menuID = 1;

            while (loggedIn) {
                Terminal.clearScreen(); // refresh UI
                System.out.println("Welcome, " + activeUser.getName() + ".\n");

                if (options != null) {
                    printMenu(String.valueOf(menuID)); // display toggle menu with various options (i.e.)
                }

                printErrorMsg(); // error message feedback
                ui.freshUI(input); // rebuild UI

                System.out.print("INPUT: ");
                String input = scanner.nextLine();

                checkInput(input); // validate input

                if (input.equals("0")) {
                    terminateApp(scanner);
                    break;
                }
            }
        }
        // ---------------------------
    }

    private static void preloadData() {
        LoadCSV.loadUsersFromCSVs();
        try {
            ProjectsDB.loadAllProjectsFromCSV("ProjectList.csv");
        } catch (Exception e) {
            System.out.println("Error loading project: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static int getTerminalWidth(String columns) {
        if (columns != null) {
            try {
                return Integer.parseInt(columns);
            } catch (NumberFormatException e) {
                return 80;
            }
        }
        return 80;
    }

    private static void printMenu(String command) {
        for (int i = 0; i < options.length; i++) {
            if (command.equals(String.valueOf(i + 1))) {
                // normal text with a pointer icon
                System.out.println("> " + options[i]);
            } else {
                // dim text
                System.out.println("\033[2m" + "  " + options[i] + "\033[0m");
            }
        }
    }

    private static void printErrorMsg() {
        if (!errorMsg.isEmpty()) {
            System.out.println("\033[31m" + errorMsg + "\033[0m");
            errorMsg = "";
        }
    }

    private static void checkInput(String input) {
        if (input.equalsIgnoreCase("logout")) {
            loggedIn = false;
            activeUser = null;
            errorMsg = "Logged out successfully.";
            return;
        }

        if (input.matches("-\\d+")) {
            errorMsg = "Invalid input, numbers cannot be negative.";
            return;
        }

        if (input.matches("\\d") && input.length() == 1) {
            int parsedInput = Integer.parseInt(input);
            if (options != null && options.length > 0) {
                if (parsedInput >= 1 && parsedInput <= options.length) {
                    menuID = parsedInput;
                } else {
                    errorMsg = "Invalid input, enter a number between 1 and " + options.length + ".";
                }
            }

        }
        if(input.toLowerCase().startsWith("changepassword ")){
            if (MainMenu.activeUser == null) {
                MainMenu.errorMsg = "You must be logged in to change your password.";
                return;
            }
    
            String[] parts = input.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                MainMenu.errorMsg = "Please provide a new password. Usage: changepassword yourNewPassword";
                return;
            }

            String password = parts[1].trim();

            if (AuthManager.validatePassword(password)) {
                AuthManager.changePassword(password);
                LoadCSV.updateCSV(AuthManager.users);
                MainMenu.errorMsg = "Password updated successfully.";
            }
    
            
        }

        activeUser.getStrategy().handleCommand(input, menuID, ui);
    }

    private static void terminateApp(Scanner scanner) {
        errorMsg = "";
        scanner.close();
        LoadCSV.updateCSV(AuthManager.users);
        try {
            ProjectsDB.storeProjects("ProjectList.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Terminal.clearScreen();
        System.out.println("APP TERMINATED SUCCESSFULLY");
        System.exit(0);
    }
}
