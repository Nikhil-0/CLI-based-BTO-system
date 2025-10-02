package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import appmodules.applicant.Applicant;
import appmodules.officer.HDBOfficer;
import appmodules.officer.OfficerRegistration;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Application;

public class GeneralApplicationManager {
    public static Integer width;
    public static UserInterface ui;
    private static ListItem[] listItems = {};

    private static Applicant currUser;
    private static HDBOfficer currOfficer;

    private static Application currApplication;
    private static OfficerRegistration currRegistration;

    private static BoxObject currApplicationBox, currRegistrationBox;
    private static ListObject currRequestList;

    private static Boolean viewingApplication = false;
    private static Boolean viewingRegistration = false;

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        if (MainMenu.activeUser instanceof HDBOfficer) {
            currOfficer = ((HDBOfficer) MainMenu.activeUser);
            currRequestList = new ListObject(width, "This is a list of your requests.");
            currApplicationBox = new BoxObject(width, "Your Application", "Enter 'exit' to go back");
            currRegistrationBox = new BoxObject(width, "Your Registration", "Enter 'exit' to go back");
            updateRequests();
        } else if (MainMenu.activeUser instanceof Applicant) {
            currUser = ((Applicant) MainMenu.activeUser);
            currApplicationBox = new BoxObject(width,
                    "Your Application", "Enter 'withdraw' to withdraw.");
            updateApplication();
        }
    }

    private static void updateRequests() {
        currApplication = currOfficer.getCurrentApplication();
        currRegistration = currOfficer.getOfficerRegistration();

        List<ListItem> items = new ArrayList<>();

        if (currApplication != null) {
            items.add(new ListItem(
                    "Application for " + currApplication.getProject().getProjectName(),
                    currApplication.getColor()));
        }

        if (currRegistration != null) {
            items.add(new ListItem(
                    "Registration for " + currRegistration.getProject().getProjectName(),
                    currRegistration.getColor()));
        }

        listItems = items.toArray(new ListItem[0]);
        currRequestList.setLines(listItems);
    }

    private static void updateRegistration() {
        currRegistration = currOfficer.getOfficerRegistration();

        if (currRegistration != null) {
            List<Field> parsedFields = new ArrayList<>();
            parsedFields.add(new Field("Project Name", currRegistration.getProject().getProjectName()));
            parsedFields.add(new Field("Status", currRegistration.getStatus().toString()));
            currRegistrationBox.initialiseFields(parsedFields);
        }
    }

    private static void updateApplication() {
        if (MainMenu.activeUser instanceof HDBOfficer) {
            currApplication = currOfficer.getCurrentApplication();
        } else{
            currApplication = currUser.getCurrentApplication();
        }
        
        if (currApplication != null) {
            List<Field> parsedFields = new ArrayList<>();
            parsedFields.add(new Field("Project Name", currApplication.getProject().getProjectName()));
            parsedFields.add(new Field("Date Posted", currApplication.getApplicationDate().toString()));
            parsedFields.add(new Field("Status", currApplication.getStatus().toString()));
            currApplicationBox.initialiseFields(parsedFields);
        }
    }

    public static void applyMenu() {
        if (MainMenu.activeUser instanceof HDBOfficer) {
            updateRequests();
            if (viewingApplication) {
                ui.setObject(currApplicationBox);
            } else if (viewingRegistration) {
                ui.setObject(currRegistrationBox);
            } else {
                ui.setObject(currRequestList);
            }
        } else if (MainMenu.activeUser instanceof Applicant) {
            updateApplication();
            ui.setObject(currApplicationBox);
        } 
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(Withdraw|Reg|App|Exit|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("2")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.trim().equalsIgnoreCase("withdraw")) {
            if (MainMenu.activeUser instanceof HDBOfficer) {
                currOfficer.requestWithdrawal();
            } else{
                currUser.requestWithdrawal();
            }
            updateApplication();
        } else if (command.trim().equalsIgnoreCase("app")) {
            if (currOfficer != null) {
                if (currApplication != null) {
                    viewingApplication = true;
                    updateApplication();
                } else {
                    MainMenu.errorMsg = "Error: You haven't applied for a project.";
                }
            } else {
                MainMenu.errorMsg = "Invalid command.";
            }
        } else if (command.trim().equalsIgnoreCase("reg")) {
            if (currOfficer != null) {
                if (currRegistration != null) {
                    viewingRegistration = true;
                    updateRegistration();
                } else {
                    MainMenu.errorMsg = "Error: You haven't registered for a project.";
                }
            } else {
                MainMenu.errorMsg = "Invalid command.";
            }
        } else if(command.trim().equalsIgnoreCase("exit")){
            if (currOfficer != null) {
                if (viewingApplication || viewingRegistration) {
                    viewingApplication = false;
                    viewingRegistration = false;
                }
            } else {
                MainMenu.errorMsg = "Invalid command.";
            }
        }
    }

}
