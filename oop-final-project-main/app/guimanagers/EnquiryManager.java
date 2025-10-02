package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import app.loaders.ProjectsDB;
import appmodules.manager.HDBManager;
import appmodules.officer.HDBOfficer;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Enquiry;
import models.Project;

public class EnquiryManager {
    private static List<Project> projects = new ArrayList<>();
    private static ListItem[] listItems = {};

    public static Integer width;
    public static UserInterface ui;
    private static Boolean viewingEnquiries = false;
    private static Boolean viewingEnquiry = false;
    private static List<Enquiry> currEnquiries;
    private static Enquiry currEnquiry;

    private static HDBOfficer currOfficer;
    private static HDBManager currManager;

    private static ListObject currProjectList, currEnquiryList;
    private static BoxObject currEnquiryBox;
    private static String initNavMessageMain = "This is the enquiry manager.";
    private static String initNavMessageProj = "Toggle enquiries for ";
    private static String initNavMessageEnq = "Command: 'respond'";

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;

        if (MainMenu.activeUser instanceof HDBOfficer) {
            currOfficer = (HDBOfficer) MainMenu.activeUser;
            Project projectHandled = currOfficer.getProjectHandled();
            if(projectHandled!= null){
                currEnquiryList = new ListObject(widthVal,
                initNavMessageProj + projectHandled.getProjectName());
                viewEnquiriesFor(currOfficer.getProjectHandled());
            } else{
                currEnquiryList = new ListObject(widthVal, "No project to view enquiries for.");
            }
            
           
        } else if (MainMenu.activeUser instanceof HDBManager) {
            currManager = (HDBManager) MainMenu.activeUser;
            currProjectList = new ListObject(width, initNavMessageMain);
            updateProjects();
        }
    }

    public static void applyMenu() {
        if (viewingEnquiry) {
            ui.setObject(currEnquiryBox);
            return;
        }

        if (currOfficer != null) {
            ui.setObject(currEnquiryList);
        } else if (currManager != null) {
            ui.setObject(viewingEnquiries ? currEnquiryList : currProjectList);
        }
    }

    private static void updateProjects() {
        projects = ProjectsDB.getProjects();
        listItems = projects.stream()
                .map(p -> new ListItem(p.getProjectName(), p.getColor()))
                .toArray(ListItem[]::new);
        currProjectList.setLines(listItems);
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(View|Select|Respond|Exit|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) ) {
            if(currOfficer != null && !command.equals("5")){
                MainMenu.errorMsg = "Incorrect command. Please try again.";
            }
            if(currManager != null && !command.equals("4")){
                MainMenu.errorMsg = "Incorrect command. Please try again.";
            }
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("view ")) {
            if (currManager != null) {
                viewEnquiriesByName(command.substring(5));
            } else if (currOfficer != null) {
                MainMenu.errorMsg = "Invalid command.";
            }
        } else if (command.startsWith("select ") && viewingEnquiries) {
            viewEnquiry(command.substring(7));
        } else if (command.startsWith("respond ") && viewingEnquiry) {
            respondToEnquiry(command.substring(8));
        } else if (command.equalsIgnoreCase("exit")) {
            if (viewingEnquiry) {
                viewingEnquiry = false;
                applyMenu();
            } else if (viewingEnquiries) {
                viewingEnquiries = false;
                applyMenu();
            }
        }
    }

    private static void viewEnquiriesByName(String projectName) {
        Project currProject = ProjectsDB.findProjectByName(projectName);
        viewEnquiriesFor(currProject);
    }

    private static void viewEnquiriesFor(Project currProject) {
        if (currProject != null) {
            viewingEnquiries = true;
            currEnquiryList = new ListObject(width, initNavMessageProj + currProject.getProjectName());
            currEnquiries = currProject.getEnquiries();
            listItems = currEnquiries.stream()
                    .map(p -> new ListItem(p.getSubject(), p.getColor()))
                    .toArray(ListItem[]::new);
            currEnquiryList.setLines(listItems);
        } else {
            MainMenu.errorMsg = "Project not found.";
            return;
        }
    }

    private static void viewEnquiry(String id) {
        Integer numId;
        try {
            numId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            numId = null;
        }
        if (numId != null) {
            viewingEnquiry = true;
            currEnquiry = currEnquiries.get(numId - 1);
            currEnquiryBox = new BoxObject(width, currEnquiry.getSubject(), initNavMessageEnq);
            setEnquiryFields();
        } else {
            MainMenu.errorMsg = "Enquiry not found. Did you enter the wrong index?";
        }
    }

    private static void respondToEnquiry(String response) {
        currEnquiry.setAnswer(response, currOfficer != null ? currOfficer : currManager);
        setEnquiryFields();
        listItems = currEnquiries.stream()
                .map(p -> new ListItem(p.getSubject(), p.getColor()))
                .toArray(ListItem[]::new);
        currEnquiryList.setLines(listItems);
    }

    private static void setEnquiryFields() {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("Content", currEnquiry.getContent()));
        parsedFields.add(new Field("Answered?", currEnquiry.getAnswered()));
        parsedFields.add(new Field("Answer", currEnquiry.getAnswer()));
        currEnquiryBox.initialiseFields(parsedFields);
    }
}