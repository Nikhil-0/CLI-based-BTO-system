package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import appmodules.applicant.Applicant;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Enquiry;

public class ApplicantEnquiriesManager {
    private static List<Enquiry> myEnquiries = new ArrayList<>();
    private static ListItem[] listItems = {};

    public static Integer width;
    public static UserInterface ui;

    private static Applicant currUser;
    private static Enquiry currEnquiry;

    private static ListObject currEnquiryList;
    private static BoxObject currEnquiryBox;

    private static Boolean viewingEnquiry = false;

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        
        if (MainMenu.activeUser instanceof Applicant) {
            currUser = ((Applicant) MainMenu.activeUser);
        }

        currEnquiryList = new ListObject(width, "This is the list of your enquiries.");
        myEnquiries = currUser.getEnquiries();

        updateEnquiries();
    }

    private static void updateEnquiries() {
        myEnquiries = currUser.getEnquiries();
        if (myEnquiries.size() != 0) {
            listItems = myEnquiries.stream()
                    .map(e -> new ListItem(e.getSubject(), e.getColor()))
                    .toArray(ListItem[]::new);
            currEnquiryList.setLines(listItems);
        } else{
            currEnquiryList.setLines(null);
        }
    }

    public static void applyMenu() {
        updateEnquiries();
        if (viewingEnquiry) {
            ui.setObject(currEnquiryBox);
            return;
        } else {
            ui.setObject(currEnquiryList);
            return;
        }
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(View|Exit|Edit|Delete|Subject|Content|Confirm|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("3")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("view ")) {
            getEnquiry(command.substring(5));
            if (currEnquiry != null) {
                viewingEnquiry = true;
                currEnquiryBox = new BoxObject(width, "Viewing " + currEnquiry.getSubject(),
                        "Enter 'exit' to go back.");
                setEnquiryFields();
            } else {
                MainMenu.errorMsg = "Enquiry not found! Did you enter the wrong index?";
            }
        } else if (command.startsWith("edit ")) {
            getEnquiry(command.substring(5));
            if (currEnquiry != null) {
                viewingEnquiry = true;
                currEnquiryBox = new BoxObject(width, "Viewing Enquiry",
                        "Command: [FIELD] [VALUE]. Enter 'exit' to go back.");
                setEnquiryFields();
            } else {
                MainMenu.errorMsg = "Enquiry not found! Did you enter the wrong index?";
            }
        } else if (command.startsWith("subject ") && viewingEnquiry) {
            currEnquiry.setSubject(command.substring(8));
            setEnquiryFields();
        } else if (command.startsWith("content ") && viewingEnquiry) {
            currEnquiry.setContent(command.substring(8));
            setEnquiryFields();
        }else if (command.equalsIgnoreCase("confirm") && viewingEnquiry) {
            currUser.deleteMyEnquiry(currEnquiry);
            currUser.submitEnquiry(currEnquiry.getProject(), currEnquiry);
            viewingEnquiry = false;
            MainMenu.errorMsg = "Enquiry edited successfully.";
        } else if (command.startsWith("delete ")) {
            getEnquiry(command.substring(7));
            if (currEnquiry != null) {
                currUser.deleteMyEnquiry(currEnquiry);
                MainMenu.errorMsg = "Enquiry deleted successfully.";
            } else {
                MainMenu.errorMsg = "Enquiry not found! Did you enter the wrong index?";
            }
        } else if (command.equalsIgnoreCase("exit")) {
            if (viewingEnquiry) {
                viewingEnquiry = false;
            }
        }
    }

    private static void getEnquiry(String enquiryID) {
        try {
            currEnquiry = currUser.findMyEnquiryByIndex(Integer.parseInt(enquiryID));
        } catch (NumberFormatException e) {
            currEnquiry = null;
        }
    }

    private static void setEnquiryFields() {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("Subject", currEnquiry.getSubject()));
        parsedFields.add(new Field("Content", currEnquiry.getContent()));
        parsedFields.add(new Field("Answered?", currEnquiry.getAnswered()));
        parsedFields.add(new Field("Answer", currEnquiry.getAnswer()));
        parsedFields.add(new Field("Replier", currEnquiry.getReplier().getName()));
        currEnquiryBox.initialiseFields(parsedFields);
    }

}
