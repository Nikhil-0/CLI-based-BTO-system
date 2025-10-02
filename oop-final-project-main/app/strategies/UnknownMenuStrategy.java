package app.strategies;

import gui.UserInterface;
import gui.components.ListObject;
import main.MainMenu;

public class UnknownMenuStrategy implements MenuStrategy {
    @Override
    public void initialize(Integer width, UserInterface ui) {
        ui.setObject(new ListObject(width, "No menu available."));
    }

    @Override
    public void handleCommand(String input, Integer numericInput, UserInterface ui) {
        MainMenu.errorMsg = "Invalid input.";
    }
}
