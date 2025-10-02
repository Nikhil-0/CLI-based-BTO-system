package app.guimanagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.FileGenerator;
import appmodules.applicant.Status;
import appmodules.applicant.FlatType;
import appmodules.officer.HDBOfficer;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Application;
import models.Project;

public class OfficerFlatManager {
    public static Integer width;
    public static UserInterface ui;

    private static HDBOfficer currUser;
    private static Project projectHandled;
    private static List<Application> applications;
    private static ListItem[] listItems = {};
    private static Boolean viewingApplication = false;

    private static ListObject currApplicationsList;
    private static BoxObject currApplicationBox;
    private static Application currApplication;

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;

        if (MainMenu.activeUser instanceof HDBOfficer) {
            currUser = ((HDBOfficer) MainMenu.activeUser);
        }

        projectHandled = currUser.getProjectHandled();
        if (projectHandled != null) {
            applications = projectHandled.getApplications();
        }
        currApplicationsList = new ListObject(width, "");

        updateApplications();
    }

    private static void updateApplications() {
        if (projectHandled != null) {
            currApplicationsList.setInitNavMessage(
                    "This is a list of applications for " + currUser.getProjectHandled().getProjectName());
            if (applications.size() != 0) {
                listItems = applications.stream()
                        .map(a -> new ListItem("Application " + a.getApplicant().getNric(), a.getColor()))
                        .toArray(ListItem[]::new);
                currApplicationsList.setLines(listItems);
            } else {
                currApplicationsList.setLines(null);
            }
        } else {
            currApplicationsList.setInitNavMessage("Successfully register for a project to view applications.");
            currApplicationsList.setLines(null);
        }
    }

    public static void applyMenu() {
        updateApplications();
        if (viewingApplication) {
            ui.setObject(currApplicationBox);
            return;
        } else {
            ui.setObject(currApplicationsList);
            return;
        }
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(View|Exit|Book|Receipt|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("4")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("view ")) {
            getApplication(command.substring(5).toUpperCase());
        } else if (command.trim().equalsIgnoreCase("book") && viewingApplication) {
            currUser.bookFlatForApplicant(currApplication.getApplicant().getNric());
        } else if (command.trim().equalsIgnoreCase("receipt") && viewingApplication) {
            if (currApplication.getStatus() == Status.BOOKED) {
                Map<String, String> receipt = currUser.generateApplicantReceipt(currApplication.getApplicant());
                FileGenerator.writeReceiptToFile(receipt, "receipt.txt", "Flat Selection");
            } else {
                MainMenu.errorMsg = "Flat type has not been booked yet.";
            }
        } else if (command.trim().equalsIgnoreCase("exit")) {
            if (viewingApplication) {
                viewingApplication = false;
            }
        }
    }

    private static void getApplication(String nric) {
        currApplication = Application.getApplicationFromNric(nric, projectHandled);
        if (currApplication != null) {
            currApplicationBox = new BoxObject(width, "Application " + currApplication.getApplicant().getNric(),
                    "Enter 'book' to confirm the flat type.");
            List<Field> parsedFields = new ArrayList<>();
            parsedFields.add(new Field("Applicant Name", currApplication.getApplicant().getName()));
            parsedFields.add(new Field("Applicant Age", String.valueOf(currApplication.getApplicant().getAge())));
            parsedFields.add(new Field("Applicant Marital Status", currApplication.getApplicant().getMaritalStatus()));
            parsedFields.add(new Field("Project Applied For", currApplication.getProject().getProjectName()));

            FlatType flatType = currApplication.getFlatType();
            parsedFields.add(new Field("Flat Type", (flatType != null) ? flatType.toString() : null));

            parsedFields.add(new Field("Applied On", currApplication.getApplicationDate().toString()));
            parsedFields.add(new Field("Status", currApplication.getStatus().toString()));
            currApplicationBox.initialiseFields(parsedFields);
            viewingApplication = true;
        } else {
            MainMenu.errorMsg = "Application not found. Did you enter the NRIC incorrectly?";
        }
    }

}
