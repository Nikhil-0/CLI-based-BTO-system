package main.commands;

import main.MainMenu;
import models.User;

import java.util.List;

import gui.UserInterface;
import gui.components.ListObject;

public abstract class StandardCommand{
    public abstract boolean matches(String input);
    public abstract void execute(String input);
    private static UserInterface ui = UserInterface.getInstance();

    public static final List<StandardCommand> commands = List.of(
        new Logout(),
        new Negative(),
        new SubMenuSelect(),
        new ToggleList(),
        new StrategySpecificCommands(),
        new ChangePassword()
        
    );

    public static class Logout extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return input.equalsIgnoreCase("logout");
        }

        @Override
        public void execute(String input) {
            MainMenu.loggedIn = false;
            MainMenu.activeUser = null;
            MainMenu.errorMsg = "Logged out successfully.";
        }
    }

    public static class ChangePassword extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return input.toLowerCase().startsWith("changepassword ");
        }
    
        @Override 
        public void execute(String input) {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                MainMenu.errorMsg = "Please provide a new password. Usage: changepassword yourNewPassword";
                return;
            }
    
            String newPassword = parts[1].trim();
            MainMenu.activeUser.setPassword(newPassword);
            MainMenu.errorMsg = "Password updated successfully.";
        }
    }
    

    public static class ToggleList extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return ui.getObject() == ListObject.class;
        }

        @Override
        public void execute(String input) {
            ui.getUIObject().updateIndex(input);
        }
    }

    public static class Negative extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return input.matches("-\\d+");
        }

        @Override
        public void execute(String input) {
            MainMenu.errorMsg = "Invalid input, numbers cannot be negative.";
        }
    }

    public static class SubMenuSelect extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return input.matches("\\d") && input.length() == 1;
        }

        @Override
        public void execute(String input) {
            int parsedInput = Integer.parseInt(input);
            if (MainMenu.options != null && MainMenu.options.length > 0) {
                if (parsedInput >= 1 && parsedInput <= MainMenu.options.length) {
                    MainMenu.menuID = parsedInput;
                } else {
                    MainMenu.errorMsg = "Invalid input, enter a number between 1 and " + MainMenu.options.length + ".";
                }
            }
        }
    }

    public static class StrategySpecificCommands extends StandardCommand {
        @Override
        public boolean matches(String input) {
            return true; // catch-all
        }

        @Override
        public void execute(String input) {
            User activeUser = MainMenu.activeUser;
            activeUser.getStrategy().handleCommand(input, MainMenu.menuID, ui);
        }
    }
}
