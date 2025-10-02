package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.loaders.ProjectsDB;
import appmodules.applicant.Applicant;
import appmodules.applicant.FlatType;
import appmodules.manager.HDBManager;
import appmodules.officer.HDBOfficer;
import appmodules.officer.OfficerRegistration;
import main.MainMenu;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private String projectName;
    private String neighborhood;

    private Map<FlatType, Integer> totalUnits;
    private Map<FlatType, Integer> remainingUnits;

    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;

    private HDBManager manager;
    private List<HDBOfficer> officers;
    private int maxOfficerSlots = 10;

    private boolean visible;
    private String color;

    private List<Application> applications;
    private List<Enquiry> enquiries;
    private List<OfficerRegistration> registrations;

    private Map<FlatType, Double> sellingPrices;
    private List<String> officerNames;

    public Project(String projectName, String neighborhood, Map<FlatType, Integer> totalUnits,
            LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
            HDBManager manager, int maxOfficerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.totalUnits = new HashMap<>(totalUnits);
        this.remainingUnits = new HashMap<>(totalUnits); // Initially all units are available
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.manager = manager;
        this.maxOfficerSlots = maxOfficerSlots;
        this.visible = true;
        this.officers = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.enquiries = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.sellingPrices = new HashMap<>();
        this.officerNames = new ArrayList<>();
    }

    public String getColor() {
        return color;
    }

    public void removeApplication(Application application) {
        this.applications.remove(application);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public boolean hasFlatType(FlatType flatType) {
        return totalUnits.containsKey(flatType) && totalUnits.get(flatType) > 0;
    }

    public int getTotalUnits(FlatType flatType) {
        return totalUnits.getOrDefault(flatType, 0);
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setUnits(Map<FlatType, Integer> totalUnits) {
        for (FlatType type : totalUnits.keySet()) {
            int oldRemaining = remainingUnits.getOrDefault(type, 0);
            int oldTotal = this.totalUnits.getOrDefault(type, 0);
            int newTotal = totalUnits.get(type);

            int newRemaining = Math.min(oldRemaining, newTotal);
            if (oldTotal == 0) {
                newRemaining = newTotal;
            }

            this.totalUnits.put(type, newTotal);
            this.remainingUnits.put(type, newRemaining);
        }
        List<FlatType> typesToRemove = new ArrayList<>();
        for (FlatType type : this.totalUnits.keySet()) {
            if (!totalUnits.containsKey(type)) {
                typesToRemove.add(type);
            }
        }
        for (FlatType type : typesToRemove) {
            this.totalUnits.remove(type);
            this.remainingUnits.remove(type);
        }
    }

    public void setOpeningDate(LocalDate date) {
        this.applicationOpeningDate = date;
    }

    public void setClosingDate(LocalDate date) {
        this.applicationClosingDate = date;
    }

    public Map<FlatType, Integer> getTotalUnits() {
        return totalUnits;
    }

    public void setOfficerSlots(Integer maxOfficerSlots) {
        if (maxOfficerSlots < officers.size()) {
            MainMenu.errorMsg = "Cannot reduce max slots below current number of officers (" + officers.size() + ").";
            return;
        }
        this.maxOfficerSlots = maxOfficerSlots;
    }

    public int getRemainingUnits(FlatType flatType) {
        return remainingUnits.getOrDefault(flatType, 0);
    }

    public void decrementRemainingUnits(FlatType flatType) {
        if (remainingUnits.containsKey(flatType) && remainingUnits.get(flatType) > 0) {
            remainingUnits.put(flatType, remainingUnits.get(flatType) - 1);
        } else {
            MainMenu.errorMsg = "Selected unit unavailable. Booking failed.";
        }
    }

    public void incrementRemainingUnits(FlatType flatType) {
        if (remainingUnits.containsKey(flatType)) {
            int currentRemaining = remainingUnits.get(flatType);
            int totalForType = totalUnits.get(flatType);

            if (currentRemaining < totalForType) {
                remainingUnits.put(flatType, currentRemaining + 1);
            }
        }
    }

    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public boolean isWithinApplicationPeriod() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(applicationOpeningDate) && !today.isAfter(applicationClosingDate);
    }

    public HDBManager getManager() {
        return manager;
    }

    public List<HDBOfficer> getOfficers() {
        return new ArrayList<>(officers);
    }

    public int getMaxOfficerSlots() {
        return maxOfficerSlots;
    }

    public int getRemainingOfficerSlots() {
        return maxOfficerSlots - officers.size();
    }

    public boolean addOfficer(HDBOfficer officer) {
        if (officers.contains(officer)) {
            MainMenu.errorMsg = "Officer is already assigned to this project.";
            return false;
        }

        if (officers.size() >= maxOfficerSlots) {
            MainMenu.errorMsg = "Cannot add officer. Maximum number of officers (" + maxOfficerSlots + ") reached.";
            return false;
        }

        officers.add(officer);
        return true;
    }

    public boolean getVisibility() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisibleForApplicant() {
        return visible;
    }

    public void addApplication(Application application) {
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }

    public static List<Project> getRelevantProjects(List<Project> projects, User currUser) {
        return projects.stream()
                .filter(project -> {
                    String status = currUser.getMaritalStatus();
                    int age = currUser.getAge();

                    if (status == null || age == 0)
                        return false;

                    Map<FlatType, Integer> units = project.getTotalUnits();
                    boolean has2Room = units.containsKey(FlatType.TWO_ROOM) && units.get(FlatType.TWO_ROOM) > 0;
                    boolean has3Room = units.containsKey(FlatType.THREE_ROOM) && units.get(FlatType.THREE_ROOM) > 0;

                    if (status.equalsIgnoreCase("Single")) {
                        return age >= 35 && has2Room;
                    } else if (status.equalsIgnoreCase("Married")) {
                        return age >= 21 && (has2Room || has3Room);
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public List<Applicant> getApplicants() {
        return this.getApplications()
                .stream()
                .map(Application::getApplicant)
                .toList();
    }

    public void addEnquiry(Enquiry enquiry) {
        if (!enquiries.contains(enquiry)) {
            enquiries.add(enquiry);
        }
    }

    public void addRegistration(OfficerRegistration registration) {
        if (!registrations.contains(registration)) {
            registrations.add(registration);
        } else {
            MainMenu.errorMsg = "You have already registered!";
        }
    }

    public void removeRegistration(OfficerRegistration registration) {
        if (registrations.contains(registration)) {
            registrations.remove(registration);
        } else {
            MainMenu.errorMsg = "Error: Registration cannot be found for deletion.";
        }
    }

    public List<OfficerRegistration> getRegistrations() {
        return registrations;
    }

    public boolean removeEnquiry(Enquiry enquiry) {
        return enquiries.remove(enquiry);
    }

    public void setProjectName(String name) {
        String uniqueName = name;
        int suffix = 2;

        while (!isProjectNameUnique(name)) {
            uniqueName = name + " " + suffix;
            suffix++;
        }

        this.projectName = uniqueName;
    }

    private boolean isProjectNameUnique(String name) {
        for (Project project : ProjectsDB.getProjects()) {
            if (project.getProjectName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public List<Enquiry> getEnquiries() {
        return new ArrayList<>(enquiries);
    }

    public Map<FlatType, Double> getSellingPrices() {
        return new HashMap<>(sellingPrices);
    }

    public void setSellingPrices(Map<FlatType, Double> sellingPrices) {
        this.sellingPrices = new HashMap<>(sellingPrices);
    }

    public List<String> getOfficerNames() {
        return new ArrayList<>(officerNames);
    }

    public void setOfficerNames(List<String> officerNames) {
        this.officerNames = new ArrayList<>(officerNames);
    }

    public int getTotalUnitsForFlat(FlatType flatType) {
        return totalUnits.getOrDefault(flatType, 0);
    }
}
