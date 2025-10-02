package appmodules.applicant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.MainMenu;
import models.Application;
import models.Enquiry;
import models.Project;
import models.User;

public class Applicant extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Application currentApplication;
    private FlatBooking currentFlatBooking;
    private List<Enquiry> myEnquiries;
    private String flatBooked;

    public Applicant(String name, String nric, String password, String maritalStatus, Integer age) {
        super(name, nric, password, maritalStatus, age, "Applicant");
        this.myEnquiries = new ArrayList<>();
    } 

    public boolean applyForProject(Project project) {
        if (currentApplication != null) {
            MainMenu.errorMsg = "Error: You have already applied for a project.";
            return false;
        }

        if (!isEligibleForProject(project)) {
            MainMenu.errorMsg = "You are not eligible to apply for this project.";
            return false;
        }

        if (!project.isVisibleForApplicant()) {
            MainMenu.errorMsg = "You cannot apply for this project.";
            return false;
        }

        if (!project.isWithinApplicationPeriod()) {
            MainMenu.errorMsg = "The application period for this project has closed.";
            return false;
        }

        currentApplication = new Application(this, project, Status.PENDING, null);
        return true;
    }

    public boolean isEligibleForProject(Project project) {
        if (getMaritalStatus().equals("Single")) {
            if (getAge() < 35) {
                return false;
            }
            return project.hasFlatType(FlatType.TWO_ROOM);
        }

        else if (getMaritalStatus().equals("Married")) {
            return getAge() >= 21 && project.hasFlatType(FlatType.THREE_ROOM) && project.hasFlatType(FlatType.TWO_ROOM);
        }
        return false;
    }

    public boolean bookFlat(FlatType flatType) {
        if (currentApplication == null || currentApplication.getStatus() != Status.SUCCESSFUL) {
            return false;
        }

        if (currentFlatBooking != null) {
            return false;
        }

        Project project = currentApplication.getProject();
        if (!project.hasFlatType(flatType) || project.getRemainingUnits(flatType) <= 0) {
            return false;
        }
        currentFlatBooking = new FlatBooking(this, project, flatType);
        currentApplication.setStatus(Status.BOOKED);
        project.decrementRemainingUnits(flatType);
        return true;
    }

    public boolean requestWithdrawal() {
        if (currentApplication == null) {
            return false;
        }
        currentApplication.setStatus(Status.WITHDRAWAL_PENDING);
        return true;
    }

    public Enquiry submitEnquiry(Project project, Enquiry enquiry) {
        myEnquiries.add(enquiry);
        project.addEnquiry(enquiry);
        return enquiry;
    }

    public Enquiry findMyEnquiryByIndex(Integer numID) {
        Enquiry enquiry = myEnquiries.get(numID - 1);
        if (enquiry != null){
            return enquiry;
        } else{
            return null;
        }
    }

    public void deleteMyEnquiry(Enquiry enquiry) {
        myEnquiries.remove(enquiry);
        enquiry.getProject().removeEnquiry(enquiry);
    }

    public Application getCurrentApplication() {
        return currentApplication;
    }

    public void setApplicationStatus(Status status) {
        if (currentApplication != null) {
            currentApplication.setStatus(status);

            if (status == Status.UNSUCCESSFUL) {
                currentApplication = null;
            }
        }
    }

    public FlatBooking getCurrentFlatBooking() {
        return currentFlatBooking;
    }

    public void setFlatBooked(String flatBooked) {this.flatBooked = flatBooked;}

    public String getFlatBooked() {return flatBooked;}

    public List<Enquiry> getEnquiries() {
        return new ArrayList<>(myEnquiries);
    }

    public void approveWithdrawal() {
        if (currentApplication != null &&
                (currentApplication.getStatus() == Status.WITHDRAWAL_PENDING)) {

            // If got flat booking, return unit to available units
            if (currentFlatBooking != null) {
                Project project = currentApplication.getProject();
                project.incrementRemainingUnits(currentFlatBooking.getFlatType());
                currentFlatBooking = null;
            }

            currentApplication = null;
        }
    }
}
