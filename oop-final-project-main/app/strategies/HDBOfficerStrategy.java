package app.strategies;

import app.guimanagers.GeneralApplicationManager;
import app.guimanagers.EnquiryManager;
import app.guimanagers.ApplicantEnquiriesManager;
import app.guimanagers.OfficerFlatManager;
import app.guimanagers.BrowsingManager;
import gui.UserInterface;
import main.MainMenu;

public class HDBOfficerStrategy implements MenuStrategy {
    @Override
    public void initialize(Integer width, UserInterface ui) {
        BrowsingManager.initialize(width, ui);
        GeneralApplicationManager.initialize(width, ui);
        ApplicantEnquiriesManager.initialize(width, ui);
        OfficerFlatManager.initialize(width, ui);
        EnquiryManager.initialize(width, ui);
        MainMenu.options = new String[]{
            "View Projects (ENTER 1)",
            "Manage MY Application/Withdrawal (ENTER 2)",
            "My Enquiries (ENTER 3)",
            "Flat Selection Dashboard (ENTER 4)",
            "Manage Project Enquiries (ENTER 5)\n"
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
            case 4 -> {
                OfficerFlatManager.processCommand(input);
                OfficerFlatManager.applyMenu();
            }
            case 5 -> {
                EnquiryManager.processCommand(input);
                EnquiryManager.applyMenu();
            }
            default -> MainMenu.errorMsg = "Invalid input.";
        }
    }
}
