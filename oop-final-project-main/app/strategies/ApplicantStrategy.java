package app.strategies;

import app.guimanagers.GeneralApplicationManager;
import app.guimanagers.ApplicantEnquiriesManager;
import app.guimanagers.BrowsingManager;
import gui.UserInterface;
import main.MainMenu;

public class ApplicantStrategy implements MenuStrategy {
    @Override
    public void initialize(Integer width, UserInterface ui) {
        BrowsingManager.initialize(width, ui);
        ApplicantEnquiriesManager.initialize(width, ui);
        GeneralApplicationManager.initialize(width, ui);
        MainMenu.options = new String[]{
            "View Projects (ENTER 1)",
            "Manage Applications/Withdrawals (ENTER 2)",
            "My Enquiries (ENTER 3)\n"
        };
    }

    @Override
    public void handleCommand(String input, Integer numericInput, UserInterface ui) {
        switch (numericInput) {
            case 1 -> {
                BrowsingManager.processCommand(input);
                BrowsingManager.applyMenu();
            }
            case 2 -> {
                GeneralApplicationManager.processCommand(input);
                GeneralApplicationManager.applyMenu();
            }
            case 3 -> {
                ApplicantEnquiriesManager.processCommand(input);
                ApplicantEnquiriesManager.applyMenu();
            }
            default -> MainMenu.errorMsg = "Invalid input.";
        }
    }
}

