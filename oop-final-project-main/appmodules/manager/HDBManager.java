package appmodules.manager;

import java.time.LocalDate;

import app.guimanagers.AuthManager;
import main.MainMenu;
import models.Project;
import models.User;

public class HDBManager extends User {
    private Project projectManaged;

    public HDBManager(String name, String nric, String password, String maritalStatus, Integer age) {
        super(name, nric, password, maritalStatus, age, "HDB Manager");
    }

    public static HDBManager getManagerByName(String managerName) {
        for (User user : AuthManager.users) {
            if (user instanceof HDBManager && user.getName().equalsIgnoreCase(managerName)) {
                return (HDBManager) user;
            }
        }
        return null;
    }

    public void setProjectManaged(Project project){
        this.projectManaged = project;
    }

    public Project getProjectManaged(){
        return projectManaged;
    }

    public Boolean canManageProject() {
        if (projectManaged != null) {
            LocalDate today = LocalDate.now();
            LocalDate open = projectManaged.getApplicationOpeningDate();
            LocalDate close = projectManaged.getApplicationClosingDate();

            if ((today.isEqual(open) || today.isAfter(open)) &&
                    (today.isEqual(close) || today.isBefore(close))) {
                MainMenu.errorMsg = "You are already managing a project during this time.";
                return false;
            } else{
                return true;
            }
        }

        return true;
    }
}
