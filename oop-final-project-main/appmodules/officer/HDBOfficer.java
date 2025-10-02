package appmodules.officer;

import java.util.HashMap;
import java.util.Map;

import app.guimanagers.AuthManager;
import appmodules.applicant.Applicant;
import appmodules.applicant.Status;
import appmodules.applicant.FlatType;
import gui.ColorPalette;
import main.MainMenu;
import models.Application;
import models.Project;
import models.User;

public class HDBOfficer extends Applicant {
    private Project projectHandled;
    private OfficerRegistration officerRegistration;
    private Application currApplication;

    public HDBOfficer(String name, String nric, String password, String maritalStatus, Integer age) {
        super(name, nric, password, maritalStatus, age);
        this.setRole("HDB Officer");
        currApplication = this.getCurrentApplication();
    }

    public static HDBOfficer getOfficerByName(String officerName) {
        for (User user : AuthManager.users) {
            if (user instanceof HDBOfficer && user.getName().equalsIgnoreCase(officerName)) {
                return (HDBOfficer) user;
            }
        }
        return null;
    }

    public Boolean isHandlingProject(){
        if (projectHandled != null){
            return true;
        }
        return false;
    }

    @Override
    public boolean isEligibleForProject(Project project) {
        if (project == this.projectHandled) {
            MainMenu.errorMsg = "You cannot apply for a project you are handling.";
            return false;
        }
        return super.isEligibleForProject(project);
    }

    public boolean registerForProject(Project project) {
        if (projectHandled != null) {
            if(projectHandled == project){
                MainMenu.errorMsg = "You are already handling this project.";
                return false;
            } else{
                MainMenu.errorMsg = "Error: You are already handling another project.";
                return false;
            }
        
        }

        if (currApplication != null && project == currApplication.getProject()) {
            MainMenu.errorMsg = "You cannot register to handle project you have applied for.";
            return false;
        }
        if (project.getRemainingOfficerSlots() == 0) {
            MainMenu.errorMsg = "No more registration slots available.";
            return false;
        }

        if (officerRegistration != null && officerRegistration.getStatus() != Status.UNSUCCESSFUL) {
            MainMenu.errorMsg = "You have already registered";
            return false;
        }

        this.officerRegistration = new OfficerRegistration(project, this);
        project.setColor(ColorPalette.CYAN);
        return true;
    }

    public OfficerRegistration getOfficerRegistration() {
        return officerRegistration;
    }

    public Project getProjectHandled() {
        return projectHandled;
    }

    public void setProjectHandled(Project projectHandled) {
        this.projectHandled = projectHandled;
    }

    private Map<String, String> generateProjectDetails(Project project) {
        Map<String, String> projectDetails = new HashMap<>();
        projectDetails.put("Project Name", project.getProjectName());
        projectDetails.put("Neighbourhood", project.getNeighborhood());
        projectDetails.put("Manager", project.getManager().getName());
        projectDetails.put("Opening Date", project.getApplicationOpeningDate().toString());
        projectDetails.put("Closing Date", project.getApplicationClosingDate().toString());
        return projectDetails;
    }

    public Map<String, String> generateApplicantReceipt(Applicant applicant) {
        Map<String, String> receipt = new HashMap<>();
        receipt.put("Name", applicant.getName());
        receipt.put("NRIC", applicant.getNric());
        receipt.put("Age", String.valueOf(applicant.getAge()));
        receipt.put("Marital Status", applicant.getMaritalStatus());
        receipt.put("Flat Type", applicant.getCurrentApplication().getFlatType().toString());
        receipt.putAll(generateProjectDetails(applicant.getCurrentApplication().getProject()));
        return receipt;
    }

    public Map<String, String> generateOwnProjectDetails() {
        return generateProjectDetails(projectHandled);
    }



    private void bookApplication(Application application) {
        if (application.getStatus().equals(Status.SUCCESSFUL)){
            application.setStatus(Status.BOOKED);
        }
            
    }

    public void bookFlatForApplicant(String nric) {
        Application application = Application.getApplicationFromNric(nric, projectHandled);

        if (!application.getStatus().equals(Status.SUCCESSFUL)) {
            MainMenu.errorMsg = "Cannot book flat for an unsuccessful application.";
            return;
        }

        Project project = getProjectHandled();

        if (project != projectHandled) {
            MainMenu.errorMsg = "Error: This is not your project to handle.";
            return;
        }

        Applicant applicant = application.getApplicant();

        if (application.getFlatType() == FlatType.THREE_ROOM) {
            project.decrementRemainingUnits(FlatType.THREE_ROOM);
        } else if (application.getFlatType() == FlatType.TWO_ROOM) {
            project.decrementRemainingUnits(FlatType.TWO_ROOM);
        } else {
            application.setStatus(Status.UNSUCCESSFUL);
            return;
        }

        bookApplication(application);
        applicant.setFlatBooked(application.getFlatType().toString());
        MainMenu.errorMsg = "Flat type officially booked for " + applicant.getName() + ".";
    }
}
