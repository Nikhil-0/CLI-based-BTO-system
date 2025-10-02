package app.loaders;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.guimanagers.AuthManager;
import appmodules.applicant.Applicant;
import appmodules.manager.HDBManager;
import appmodules.officer.HDBOfficer;
import models.User;

public class LoadCSV {
    public static void loadUsersFromCSVs() {
        loadFromFile("ApplicantList.csv", "Applicant");
        loadFromFile("ManagerList.csv", "HDB Manager");
        loadFromFile("OfficerList.csv", "HDB Officer");
    }

    private static void loadFromFile(String fileName, String role) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 5)
                    continue;

                String name = parts[0].trim();
                String nric = parts[1].trim();
                Integer age = Integer.parseInt(parts[2].trim());
                String maritalStatus = parts[3].trim();
                String password = parts[4].trim();

                User user;
                switch (role) {
                    case "Applicant":
                        user = new Applicant(name, nric, password, maritalStatus, age);
                        break;
                    case "HDB Officer":
                        user = new HDBOfficer(name, nric, password, maritalStatus, age);
                        break;
                    case "HDB Manager":
                        user = new HDBManager(name, nric, password, maritalStatus, age);
                        break;
                    default:
                        user = new User(name, nric, password, maritalStatus, age, role);
                        break;
                }
                AuthManager.users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
            e.printStackTrace();
        }
    }

    public static void updateCSV(List<User> users) {
        Map<String, String> roleToFile = Map.of(
                "Applicant", "ApplicantList.csv",
                "HDB Manager", "ManagerList.csv",
                "HDB Officer", "OfficerList.csv");
    
        Map<String, List<User>> roleGroups = new HashMap<>();
        for (User user : users) {
            roleGroups.computeIfAbsent(user.getRole(), r -> new ArrayList<>()).add(user);
        }
    
        for (Map.Entry<String, List<User>> entry : roleGroups.entrySet()) {
            String role = entry.getKey();
            String fileName = roleToFile.get(role);
            if (fileName == null)
                continue;
    
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                // Write the header
                bw.write("Name,NRIC,Age,MaritalStatus,Password");
                bw.newLine();
    
                for (User user : entry.getValue()) {
                    String row = String.format("%s,%s,%d,%s,%s",
                            user.getName(),
                            user.getNric(),
                            user.getAge(),
                            user.getMaritalStatus(),
                            user.getPassword());
                    bw.write(row);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error writing to " + fileName + ": " + e.getMessage());
            }
        }
    }
    
}
