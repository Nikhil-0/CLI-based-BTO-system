package appmodules.applicant;

import java.io.Serializable;
import java.time.LocalDate;

import models.Project;

public class FlatBooking implements Serializable {
    private static final long serialVersionUID = 1L;

    private Applicant applicant;
    private Project project;
    private FlatType flatType;
    private LocalDate bookingDate;

    public FlatBooking(Applicant applicant, Project project, FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.bookingDate = LocalDate.now();
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /**
     * Generates a receipt for the flat booking.
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== FLAT BOOKING RECEIPT =====\n");
        receipt.append("Applicant NRIC: ").append(applicant.getNric()).append("\n");
        receipt.append("Age: ").append(applicant.getAge()).append("\n");
        receipt.append("Marital Status: ").append(applicant.getMaritalStatus()).append("\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(flatType).append("\n");
        receipt.append("Booking Date: ").append(bookingDate).append("\n");
        receipt.append("==============================\n");
        return receipt.toString();
    }
}
