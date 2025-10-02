package models;

import app.strategies.MenuStrategyProvider;
import main.MainMenu;

public class User {
    private String name, nric, password, maritalStatus, role;
    private Integer age;

    public User(String name, String nric, String password, String maritalStatus, Integer age, String role) {
        this.name = name;
        this.nric = nric;
        this.password = password;
        this.maritalStatus = maritalStatus;
        this.age = age;
        this.role = role;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getNric() {
        return nric != null ? nric : "";
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public String getMaritalStatus() {
        return maritalStatus != null ? maritalStatus : "Unknown";
    }

    public String getRole() {
        return role != null ? role : "Unknown";
    }

    public MenuStrategyProvider getStrategy() {
        return MenuStrategyProvider.fromRole(getRole());
    }

    public Integer getAge() {
        return age != null ? age : 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaritalStatus(String maritalStatus) {
        if (maritalStatus == null || maritalStatus.trim().isEmpty()) {
            System.out.println("Marital status cannot be empty.");
            return;
        }

        switch (maritalStatus.trim().toLowerCase()) {
            case "single":
                this.maritalStatus = "Single";
                break;
            case "married":
                this.maritalStatus = "Married";
                break;
            default:
                System.out.println("Invalid marital status.");
                break;
        }
    }

    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            System.out.println("Role cannot be empty.");
            return;
        }

        switch (role.trim().toLowerCase()) {
            case "manager":
            case "hdb manager":
                this.role = "HDB Manager";
                break;
            case "officer":
            case "hdb officer":
                this.role = "HDB Officer";
                break;
            case "applicant":
                this.role = "Applicant";
                break;
            default:
                System.out.println("Invalid role.");
                break;
        }
    }

    public void setAge(Integer age) {
        if (age < 21) {
            MainMenu.errorMsg = "You must be at least 21 years old to apply for a BTO.";
            return;
        }

        if (age > 130 || age <= 0) {
            MainMenu.errorMsg = "Please enter a valid age.";
            return;
        }

        this.age = age;
    }

}
