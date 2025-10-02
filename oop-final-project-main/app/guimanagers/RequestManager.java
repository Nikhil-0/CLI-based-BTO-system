package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import appmodules.applicant.Status;
import appmodules.applicant.FlatType;
import appmodules.manager.HDBManager;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Application;
import models.Project;

public class RequestManager {
    private static Integer width;
    private static UserInterface ui;

    private static HDBManager currManager;
    private static Project projectManaged;

    private static ListObject currRequestsList;
    private static BoxObject currRequestBox;

    private static List<Application> requests;
    private static Application currRequest;
    private static ListItem[] listItems = {};

    private static Boolean viewingRequest = false;

    public static void initialize(Integer widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        if (MainMenu.activeUser instanceof HDBManager) {
            currManager = (HDBManager) MainMenu.activeUser;
        }
        projectManaged = currManager.getProjectManaged();
        currRequestsList = new ListObject(width,
                projectManaged != null
                        ? "View requests and withdrawals for " + projectManaged.getProjectName() + "."
                        : "No managed project found.");
        updateRequestsList();
    }

    private static void updateRequestsList() {
        if (projectManaged != null) {
            requests = projectManaged.getApplications();
            if (requests != null){
                listItems = requests.stream()
                .map(a -> new ListItem(
                        a.getStatus() == Status.PENDING ? "Application: " + a.getApplicant().getNric()
                                : "Withdrawal: " + a.getApplicant().getNric(),
                        a.getColor()))
                .toArray(ListItem[]::new);

        currRequestsList.setLines(listItems);
            }
            
        }
    }

    public static void applyMenu() {
        if (viewingRequest) {
            setRequestFields();
            ui.setObject(currRequestBox);
            return;
        }
        updateRequestsList();
        ui.setObject(currRequestsList);
    }

    private static void setRequestFields() {
        if (currRequest != null) {
            currRequestBox = new BoxObject(width, "Application " + currRequest.getApplicant().getNric(),
                    "Enter 'approve' or 'reject'.");
            List<Field> parsedFields = new ArrayList<>();
            parsedFields.add(new Field("Applicant Name", currRequest.getApplicant().getName()));
            parsedFields.add(new Field("Applicant Age", String.valueOf(currRequest.getApplicant().getAge())));
            parsedFields.add(new Field("Applicant Marital Status", currRequest.getApplicant().getMaritalStatus()));
            parsedFields.add(new Field("Project Applied For", currRequest.getProject().getProjectName()));

            FlatType flatType = currRequest.getFlatType();
            parsedFields.add(new Field("Flat Type", (flatType != null) ? flatType.toString() : null));

            parsedFields.add(new Field("Applied On", currRequest.getApplicationDate().toString()));
            parsedFields.add(new Field("Status", currRequest.getStatus().toString()));
            currRequestBox.initialiseFields(parsedFields);
            viewingRequest = true;
        }
    }

    private static void getApplication(String nric) {
        currRequest = Application.getApplicationFromNric(nric, projectManaged);
        if (currRequest == null) {
            MainMenu.errorMsg = "Application not found. Did you enter the NRIC incorrectly?";
        }
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(View|Exit|Approve|Reject|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("3")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("view ")) {
            getApplication(command.substring(5).toUpperCase());
        } else if (command.trim().equalsIgnoreCase("approve") && viewingRequest) {
            approveRequest();
        } else if (command.trim().equalsIgnoreCase("reject") && viewingRequest) {
            rejectRequest();
        } else if (command.equalsIgnoreCase("exit")) {
            if (viewingRequest) {
                viewingRequest = false;
            }
        }
    }

    private static void approveWithdrawal() {
        removeRequest();
    }

    private static void removeRequest() {
        currRequest.deleteThis();
        currRequest = null;
        viewingRequest = false;
    }

    private static void approveRequest() {
        Status status = currRequest.getStatus();
        if (status == Status.WITHDRAWAL_PENDING && currRequest.canBeApproved()) {
            approveWithdrawal();
        } else if (status == Status.PENDING || status == Status.WITHDRAWAL_REJECTED) {
            currRequest.setStatus(Status.SUCCESSFUL);
            removeRequest();
        } else {
            MainMenu.errorMsg = "You cannot approve this application as it has already been set to "
                    + status.toString();
        }
    }

    private static void rejectRequest() {
        Status status = currRequest.getStatus();
        if (status == Status.WITHDRAWAL_PENDING) {
            currRequest.setStatus(Status.WITHDRAWAL_REJECTED);
        } else if (status == Status.PENDING) {
            currRequest.setStatus(Status.UNSUCCESSFUL);
        } else {
            MainMenu.errorMsg = "You cannot reject this application as it has already been set to "
                    + status.toString();
        }
    }
}
