package app.strategies;

import gui.UserInterface;

public interface MenuStrategy {
    void initialize(Integer width, UserInterface ui);
    void handleCommand(String input, Integer numericInput, UserInterface ui);
}
