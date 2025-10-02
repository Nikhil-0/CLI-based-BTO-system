package appmodules.officer;

import appmodules.applicant.Status;
import gui.ColorPalette;
import models.Project;

public class OfficerRegistration {
    private Project project;
    private Status status;
    private HDBOfficer officer;
    private String color;

    public void deleteThis() {
        if (project != null) {
            project.removeRegistration(this);
        }
    
        project = null;
        status = null;
        officer = null;
        color = null;
    }

    public static OfficerRegistration getRegistrationFromNric(String nric, Project project) {
        for (OfficerRegistration registration : project.getRegistrations()) {
            if (registration.getOfficer().getNric().equals(nric))
                return registration;
        }
        return null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == Status.SUCCESSFUL){
            this.color = ColorPalette.GREEN;
        } else if (status == Status.UNSUCCESSFUL){
            this.color = ColorPalette.RED;
        }
        this.status = status;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public OfficerRegistration(Project project, HDBOfficer officer) {
        this.project = project;
        this.officer = officer;
        this.status = Status.PENDING;
        this.color = ColorPalette.CYAN;
    }

    public String getColor(){
        return color;
    }

    public HDBOfficer getOfficer(){
        return officer;
    }
}
