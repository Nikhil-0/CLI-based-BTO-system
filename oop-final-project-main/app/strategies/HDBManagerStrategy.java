package app.strategies;

import app.guimanagers.EnquiryManager;
import app.guimanagers.ProjectManager;
import app.guimanagers.RegistrationManager;
import app.guimanagers.RequestManager;
import gui.UserInterface;
import main.MainMenu;

public class HDBManagerStrategy implements MenuStrategy {
    @Override
    public void initialize(Integer width, UserInterface ui) {
        ProjectManager.initialize(width, ui);
        EnquiryManager.initialize(width, ui);
        RequestManager.initialize(width, ui);
        RegistrationManager.initialize(width, ui);
        MainMenu.options = new String[]{
            "Projects Dashboard (ENTER 1)",
            "Manage Officer Registrations (ENTER 2)",
            "Manage Applicant Requests (ENTER 3)",
            "Manage All Enquiries (ENTER 4)\n"
        };
    }

    @Override
    public void handleCommand(String input, Integer numericInput, UserInterface ui) {
        switch (numericInput) {
            case 1 -> {
                ProjectManager.processCommand(input);
                ProjectManager.applyMenu();
            }
            case 2 -> {
                RegistrationManager.processCommand(input);
                RegistrationManager.applyMenu();
            }
            case 3 -> {
                RequestManager.processCommand(input);
                RequestManager.applyMenu();
            }
            case 4 -> {
                EnquiryManager.processCommand(input);
                EnquiryManager.applyMenu();
            }
            default -> MainMenu.errorMsg = "Invalid input.";
        }
    }
}
