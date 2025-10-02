package models;

import java.io.Serializable;
import java.time.LocalDate;

import appmodules.applicant.Applicant;
import appmodules.applicant.Status;
import appmodules.applicant.FlatType;
import gui.ColorPalette;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private Applicant applicant;
    private Project project;
    private Status status;
    private LocalDate applicationDate;
    private FlatType desiredFlatType;
    private String color;

    public Application(Applicant applicant, Project project, Status status, FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.status = status;
        this.applicationDate = LocalDate.now();
        this.desiredFlatType = flatType != null ? flatType : null;
        project.addApplication(this);
        this.color = ColorPalette.YELLOW;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return desiredFlatType;
    }
    
    public String getColor(){
        return color;
    }

    public Status getStatus() {
        return status;
    }

    public void setFlatType(FlatType flatType){
        if(project.hasFlatType(flatType)){
            this.desiredFlatType = flatType;
        }
    }

    public void deleteThis() {
        if (project != null) {
            project.removeApplication(this);
        }
    
        applicant = null;
        project = null;
        status = null;
        applicationDate = null;
        desiredFlatType = null;
        color = null;
    }
    

    public static Application getApplicationFromNric(String nric, Project project) {
        for (Application application : project.getApplications()) {
            if (application.getApplicant().getNric().equals(nric))
                return application;
        }
        return null;
    }

    public Boolean canBeApproved() {
        if (project == null || desiredFlatType == null) return false;
    
        Integer remaining = project.getRemainingUnits(desiredFlatType);
        return remaining != null && remaining > 0;
    }
    

    public void setStatus(Status status) {
        if (status == Status.SUCCESSFUL){
            this.color = ColorPalette.GREEN;
        } else if (status == Status.WITHDRAWAL_PENDING){
            this.color = ColorPalette.GRAY;
        } else if (status == Status.UNSUCCESSFUL){
            this.color = ColorPalette.RED;
        }
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }
}

