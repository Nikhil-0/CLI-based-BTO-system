package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import appmodules.applicant.Status;
import appmodules.applicant.FlatType;
import appmodules.manager.HDBManager;
import appmodules.officer.OfficerRegistration;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Application;
import models.Project;

public class RegistrationManager {
    private static Integer width;
    private static UserInterface ui;

    private static HDBManager currManager;
    private static Project projectManaged;

    private static ListObject currRegistrationsList;
    private static BoxObject currRegistrationBox;

    private static List<OfficerRegistration> registrations;
    private static OfficerRegistration currRegistration;
    private static ListItem[] listItems = {};

    private static Boolean viewingRegistration = false;

    public static void initialize(Integer widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        if (MainMenu.activeUser instanceof HDBManager) {
            currManager = (HDBManager) MainMenu.activeUser;
        }
        projectManaged = currManager.getProjectManaged();
        currRegistrationsList = new ListObject(width, projectManaged != null
                ? "View registrations for " + projectManaged.getProjectName() + "."
                : "No managed project found.");
        updateRegistrationsList();
    }

    private static void updateRegistrationsList() {
        if (projectManaged != null) {
            registrations = projectManaged.getRegistrations();
            if (registrations != null) {
                listItems = registrations.stream()
                        .map(a -> new ListItem(
                                "Application: " + a.getOfficer().getNric(), a.getColor()))
                        .toArray(ListItem[]::new);

                currRegistrationsList.setLines(listItems);
            }

        }
    }

    public static void applyMenu() {
        if (viewingRegistration) {
            setRequestFields();
            ui.setObject(currRegistrationBox);
            return;
        }
        ui.setObject(currRegistrationsList);
    }

    private static void setRequestFields() {
        if (currRegistration != null) {
            currRegistrationBox = new BoxObject(width, currRegistration.getOfficer().getName() + "'s Registration",
                    "Enter 'approve' or 'reject'");
            List<Field> parsedFields = new ArrayList<>();
            parsedFields.add(new Field("Applicant Name", currRegistration.getOfficer().getName()));
            parsedFields.add(new Field("Applicant Age", String.valueOf(currRegistration.getOfficer().getAge())));
            parsedFields.add(new Field("Applicant Marital Status", currRegistration.getOfficer().getMaritalStatus()));
            parsedFields.add(new Field("Project Registered For", currRegistration.getProject().getProjectName()));
            parsedFields.add(new Field("Status", currRegistration.getStatus().toString()));
            currRegistrationBox.initialiseFields(parsedFields);
            viewingRegistration = true;
        }
    }

    private static void getApplication(String nric) {
        currRegistration = OfficerRegistration.getRegistrationFromNric(nric, projectManaged);
        if (currRegistration == null) {
            MainMenu.errorMsg = "Registration not found. Did you enter the NRIC incorrectly?";
        }
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(View|Exit|Approve|Reject|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("2")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("view ")) {
            getApplication(command.substring(5).toUpperCase());
        } else if (command.trim().equalsIgnoreCase("approve") && viewingRegistration) {
            approveRegistration();
        } else if (command.trim().equalsIgnoreCase("reject") && viewingRegistration) {
            rejectRegistration();
        } else if (command.equalsIgnoreCase("exit")) {
            if (viewingRegistration) {
                viewingRegistration = false;
            }
        }
    }

    private static void removeRegistration() {
        currRegistration.deleteThis();
        currRegistration = null;
        viewingRegistration = false;
    }

    private static void approveRegistration() {
        Status status = currRegistration.getStatus();
        if (status == Status.PENDING) {
            currRegistration.setStatus(Status.SUCCESSFUL);
            removeRegistration();
            projectManaged.addOfficer(currRegistration.getOfficer());
        } else {
            MainMenu.errorMsg = "You cannot approve this registration as it has already been set to "
                    + status.toString();
        }
    }

    private static void rejectRegistration() {
        Status status = currRegistration.getStatus();
        if (status == Status.PENDING) {
            currRegistration.setStatus(Status.UNSUCCESSFUL);
            removeRegistration();
        } else {
            MainMenu.errorMsg = "You cannot reject this registration as it has already been set to "
                    + status.toString();
        }
    }
}
