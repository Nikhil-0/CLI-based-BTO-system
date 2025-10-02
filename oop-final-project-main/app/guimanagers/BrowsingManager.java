package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import app.loaders.ProjectsDB;
import appmodules.applicant.Applicant;
import appmodules.applicant.FlatType;
import appmodules.officer.HDBOfficer;
import gui.ColorPalette;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.Enquiry;
import models.Project;

public class BrowsingManager {
    private static List<Project> projects = new ArrayList<>();
    private static ListItem[] listItems = {};

    public static Integer width;
    public static UserInterface ui;

    private static ListObject currProjectList;
    private static BoxObject currMyEnquiryBox, currSelectedProjectBox;

    private static Applicant currUser;
    private static Enquiry currEnquiry;
    private static Project currSelectedProject, projectToEnquire;

    private static Boolean settingEnquiry = false;
    private static Boolean viewingSelectedProject = false;

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        currProjectList = new ListObject(width, "This is the list of available projects.");

        if (MainMenu.activeUser instanceof Applicant) {
            currUser = ((Applicant) MainMenu.activeUser);
        }
        projects = Project.getRelevantProjects(ProjectsDB.getProjects(), currUser);
        updateProjects();
        ui.setObject(currProjectList);
    }

    public static void applyMenu() {
        updateProjects();
        if (settingEnquiry) {
            ui.setObject(currMyEnquiryBox);
            return;
        } else if (viewingSelectedProject) {
            ui.setObject(currSelectedProjectBox);
            return;
        } else {
            ui.setObject(currProjectList);
            return;
        }
    }

    private static void updateProjects() {
        if (projects.size() != 0) {
            listItems = projects.stream()
                    .filter(p -> {
                        if (currUser instanceof HDBOfficer) {
                            return true;
                        }

                        if (currUser.getCurrentApplication() == null) {
                            return false;
                        }

                        return p.getVisibility() || currUser.getCurrentApplication().getProject() == p;
                    })
                    .map(p -> new ListItem(p.getProjectName(), p.getColor()))
                    .toArray(ListItem[]::new);

            currProjectList.setLines(listItems);
        }
    }

    private static boolean isCommand(String command) {
        return command
                .matches("(?i)(Apply|Enquire|View|Confirm|Subject|Content|Exit|Register|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("1")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.startsWith("apply ")) {
            Project selectedProject = ProjectsDB.findProjectByName(command.substring(6));
            if (selectedProject != null) {
                if (currUser.applyForProject(selectedProject)) {
                    selectedProject.setColor(ColorPalette.YELLOW);
                }
            } else {
                MainMenu.errorMsg = "Project not found! Did you mispell its name?";
            }
        } else if (command.startsWith("enquire ")) {
            projectToEnquire = ProjectsDB.findProjectByName(command.substring(8));
            if (projectToEnquire != null) {
                settingEnquiry = true;
                enquireThisProject(projectToEnquire);
                setEnquiryFields();
            } else {
                MainMenu.errorMsg = "Project not found! Did you mispell its name?";
            }
        } else if (command.equalsIgnoreCase("confirm") && settingEnquiry) {
            currUser.submitEnquiry(projectToEnquire, currEnquiry);
            settingEnquiry = false;
            MainMenu.errorMsg = "Enquiry submitted!";
        } else if (command.startsWith("view ")) {
            currSelectedProject = ProjectsDB.findProjectByName(command.substring(5));
            if (currSelectedProject != null) {
                viewingSelectedProject = true;
                currSelectedProjectBox = new BoxObject(width, "Viewing " + currSelectedProject.getProjectName(),
                        "Enter 'exit' to go back.");
                setProjectFields();
            } else {
                MainMenu.errorMsg = "Project not found! Did you mispell its name?";
            }
        } else if (command.startsWith("subject ") && settingEnquiry) {
            currEnquiry.setSubject(command.substring(8));
            setEnquiryFields();
        } else if (command.startsWith("content ") && settingEnquiry) {
            currEnquiry.setContent(command.substring(8));
            setEnquiryFields();
        } else if (command.startsWith("register ")) {
            if (currUser instanceof HDBOfficer) {
                currSelectedProject = ProjectsDB.findProjectByName(command.substring(9));
                HDBOfficer officer = (HDBOfficer) currUser;
                officer.registerForProject(currSelectedProject);
            }
        } else if (command.equalsIgnoreCase("exit")) {
            if (settingEnquiry || viewingSelectedProject) {
                settingEnquiry = false;
                viewingSelectedProject = false;
            }
        }
    }

    private static void enquireThisProject(Project projectToEnquire) {
        currEnquiry = new Enquiry(currUser, projectToEnquire, "", "");
        currMyEnquiryBox = new BoxObject(width, "Enquiry for " + projectToEnquire.getProjectName(),
                "Command: [FIELD] [VALUE]");

    }

    private static void setProjectFields() {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("Name", currSelectedProject.getProjectName()));
        parsedFields.add(new Field("Neighbourhood", currSelectedProject.getNeighborhood()));
        parsedFields.add(
                new Field("2-Room Total Units",
                        String.valueOf(currSelectedProject.getTotalUnitsForFlat(FlatType.TWO_ROOM))));
        parsedFields.add(new Field("2-Room Remaining Units",
                String.valueOf(currSelectedProject.getRemainingUnits(FlatType.TWO_ROOM))));
        parsedFields.add(new Field("3-Room Total Units",
                String.valueOf(currSelectedProject.getTotalUnitsForFlat(FlatType.THREE_ROOM))));
        parsedFields.add(new Field("3-Room Remaining Units",
                String.valueOf(currSelectedProject.getRemainingUnits(FlatType.THREE_ROOM))));
        parsedFields
                .add(new Field("Application Close Date", currSelectedProject.getApplicationClosingDate().toString()));

        String officerNames = String.join(",", currSelectedProject.getOfficerNames());
        parsedFields.add(new Field("Officers", officerNames));

        currSelectedProjectBox.initialiseFields(parsedFields);
    }

    private static void setEnquiryFields() {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("Subject", currEnquiry.getSubject()));
        parsedFields.add(new Field("Content", currEnquiry.getContent()));
        currMyEnquiryBox.initialiseFields(parsedFields);
    }
}
