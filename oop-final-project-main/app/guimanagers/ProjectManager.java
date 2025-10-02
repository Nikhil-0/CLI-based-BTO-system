package app.guimanagers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.filters.ProjectFilter;
import app.filters.ProjectFilterUtils;
import app.loaders.ProjectsDB;
import appmodules.applicant.FlatType;
import appmodules.manager.HDBManager;
import appmodules.officer.HDBOfficer;
import gui.UserInterface;
import gui.structures.Field;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.ListItem;
import main.MainMenu;
import models.Project;

public class ProjectManager {
    private static List<Project> projects = new ArrayList<>();
    private static ListItem[] listItems = {};

    private static ListObject currProjectsList;
    private static BoxObject currProjectBox;

    private static Integer width;
    private static UserInterface ui;

    private static Project currProject;
    private static Boolean viewingProject = false;
    private static String lastFilterCommand = "none";

    private static String initNavMessage = "Use \"create\" to create a project and \"delete\" to remove it";

    public static void initialize(Integer widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        currProjectsList = new ListObject(width, initNavMessage);
        updateProjectList("none");
        ui.setObject(currProjectsList);
    }

    private static boolean isCommand(String command) {
        return command
                .matches(
                        "(?i)(Create|Delete|Edit|Exit|Neighborhood|Opens|Closes|Type1|Type2|Slots|Assign|Visible|Filter|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command) && !command.equals("1")) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void applyMenu() {
        if (viewingProject) {
            setProjectFields();
            ui.setObject(currProjectBox);
        } else {
            updateProjectList(lastFilterCommand);
            ui.setObject(currProjectsList);
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);

        if (command.toLowerCase().startsWith("create ")) {
            String projectName = command.substring(7).trim();
            createProject(projectName);
        } else if (command.toLowerCase().startsWith("delete ")) {
            String projectName = command.substring(7).trim();
            deleteProject(projectName);
        } else if (command.toLowerCase().startsWith("edit ")) {
            String projectName = command.substring(5).trim();
            editProject(projectName);
        } else if (command.toLowerCase().startsWith("filter ")) {
            String filterCommand = command.substring(7).trim();
            updateProjectList(filterCommand);
        } else if (command.equalsIgnoreCase("exit")) {
            if (viewingProject) {
                viewingProject = false;
            }
        } else if (viewingProject) {
            String[] parts = command.split(" ", 2);

            if (parts.length < 2) {
                MainMenu.errorMsg = "Incomplete command.";
                return;
            }

            String keyword = parts[0].toLowerCase();
            String value = parts[1].trim();

            switch (keyword) {
                case "neighborhood" -> currProject.setNeighborhood(value);
                case "opens" -> currProject.setOpeningDate(LocalDate.parse(value));
                case "closes" -> currProject.setClosingDate(LocalDate.parse(value));
                case "type1" -> {
                    Map<FlatType, Integer> updatedFlatTypeMap = new HashMap<>(currProject.getTotalUnits());
                    updatedFlatTypeMap.put(FlatType.TWO_ROOM, Integer.parseInt(value));
                    currProject.setUnits(updatedFlatTypeMap);
                }
                case "type2" -> {
                    Map<FlatType, Integer> updatedFlatTypeMap = new HashMap<>(currProject.getTotalUnits());
                    updatedFlatTypeMap.put(FlatType.THREE_ROOM, Integer.parseInt(value));
                    currProject.setUnits(updatedFlatTypeMap);
                }
                case "slots" -> currProject.setOfficerSlots(Integer.parseInt(value));
                case "visible" -> currProject.setVisible(Boolean.parseBoolean(value));
                case "assign" -> {
                    assignOfficer(value);
                }
                default -> MainMenu.errorMsg = "Invalid command.";
            }
        }
    }

    private static void createProject(String name) {
        HDBManager manager = (HDBManager) MainMenu.activeUser;
        if (manager.canManageProject()) {
            if (ProjectsDB.findProjectByName(name) != null) {
                MainMenu.errorMsg = "Project already exists.";
                return;
            }

            Project project = new Project(
                    name,
                    "Default Neighborhood",
                    new HashMap<>(),
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1),
                    (HDBManager) MainMenu.activeUser,
                    3);

            ProjectsDB.getProjects().add(project);
            manager.setProjectManaged(project);
            MainMenu.errorMsg = "Project \"" + name + "\" created.";
        } else {
            MainMenu.errorMsg = "You cannot manage a new project because you are already managing "
                    + manager.getProjectManaged() + "during its application period.";
        }
    }

    private static void deleteProject(String name) {
        Project project = ProjectsDB.findProjectByName(name);
        if (project == null) {
            MainMenu.errorMsg = "Project not found.";
            return;
        }

        ProjectsDB.getProjects().remove(project);
        MainMenu.errorMsg = "Project \"" + name + "\" deleted.";
    }

    private static void editProject(String name) {
        currProject = ProjectsDB.findProjectByName(name);
        if (currProject == null) {
            MainMenu.errorMsg = "Project not found.";
            return;
        }
        viewingProject = true;
        currProjectBox = new BoxObject(width, "Editing Project: " + name, "Command: [FIELD] [VALUE]");
    }

    private static void setProjectFields() {
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("Visible?", currProject.getVisibility() ? "True" : "False"));
        fields.add(new Field("Neighborhood", currProject.getNeighborhood()));
        fields.add(new Field("Application Opens", currProject.getApplicationOpeningDate().toString()));
        fields.add(new Field("Application Closes", currProject.getApplicationClosingDate().toString()));
        fields.add(new Field("Type 1", Integer.toString(currProject.getTotalUnitsForFlat(FlatType.TWO_ROOM))));
        fields.add(new Field("Type 2", Integer.toString(currProject.getTotalUnitsForFlat(FlatType.THREE_ROOM))));
        fields.add(new Field("Manager", currProject.getManager().getName()));
        fields.add(new Field("Officer Slot", Integer.toString(currProject.getMaxOfficerSlots())));
        String officerNames = String.join(",", currProject.getOfficerNames());

        fields.add(new Field("Officers", officerNames));
        currProjectBox.initialiseFields(fields);
    }

    private static void assignOfficer(String officerName) {
        HDBOfficer officer = HDBOfficer.getOfficerByName(officerName);
        if (officer == null) {
            MainMenu.errorMsg = "Officer not found.";
            return;
        } else {
            if (officer.isHandlingProject()) {
                MainMenu.errorMsg = "This officer is already handling a different project.";
            }
            if (currProject != null) {
                officer.setProjectHandled(currProject);
                currProject.addOfficer(officer);
                MainMenu.errorMsg = "Assigned officer successfully.";
            } else {
                MainMenu.errorMsg = "Error assigning project.";
            }
        }
    }

    private static void updateProjectList(String filterCommand) {
        lastFilterCommand = filterCommand; // save filter
        projects = ProjectsDB.getProjects();
        List<Project> filtered = ProjectFilterUtils
                .filterProjects(projects != null ? projects : ProjectsDB.getProjects(), filterCommand);

        listItems = filtered.stream()
                .map(p -> new ListItem(p.getProjectName(), p.getColor()))
                .toArray(ListItem[]::new);

        currProjectsList.setLines(listItems);
    }

}
