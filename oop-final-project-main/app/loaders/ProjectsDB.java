package app.loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import appmodules.applicant.FlatType;
import appmodules.manager.HDBManager;
import appmodules.officer.HDBOfficer;
import gui.ColorPalette;
import models.Project;

public class ProjectsDB {
    private static final List<Project> projects = new ArrayList<>();

    public static List<Project> getProjects() {
        return projects;
    }

    public static void resetProjects() {
        for (Project p : projects) {
            p.setColor(ColorPalette.WHITE);
        }
    }

    public static Project findProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static void loadAllProjectsFromCSV(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                try {
                    Project project = parseProjectFromCSVLine(line);
                    projects.add(project);
                } catch (Exception e) {
                    System.out.println("Error parsing line: " + e.getMessage());
                }
            }
        }
    }

    private static Project parseProjectFromCSVLine(String line) throws Exception {
        String[] tokens = parseCSVLine(line);

        String projectName = tokens[0].trim();
        String neighborhood = tokens[1].trim();

        FlatType type1 = parseFlatType(tokens[2].trim());
        int type1Units = Integer.parseInt(tokens[3].trim());
        double type1Price = Double.parseDouble(tokens[4].trim());

        FlatType type2 = parseFlatType(tokens[5].trim());
        int type2Units = Integer.parseInt(tokens[6].trim());
        double type2Price = Double.parseDouble(tokens[7].trim());

        LocalDate openingDate = parseDate(tokens[8].trim());
        LocalDate closingDate = parseDate(tokens[9].trim());

        String managerName = tokens[10].trim();
        int officerSlots = Integer.parseInt(tokens[11].trim());

        String[] officerNames = tokens[12].replaceAll("^\"|\"$", "").split(",");

        Map<FlatType, Integer> units = Map.of(
                type1, type1Units,
                type2, type2Units);

        Map<FlatType, Double> prices = Map.of(
                type1, type1Price,
                type2, type2Price);

        HDBManager manager = HDBManager.getManagerByName(managerName);
        Project project = new Project(projectName, neighborhood, units, openingDate, closingDate, manager,
                officerSlots);
                manager.setProjectManaged(project);
        project.setSellingPrices(prices);

        // Assign officers
        List<String> officerNameList = new ArrayList<>();
        for (String name : officerNames) {
            name = name.trim();
            HDBOfficer officer = HDBOfficer.getOfficerByName(name);
            officer.setProjectHandled(project);
            project.addOfficer(officer);
            officerNameList.add(name);
        }
        project.setOfficerNames(officerNameList);

        return project;
    }

    private static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean inQuotes = false;

        for (char ch : line.toCharArray()) {
            if (ch == '\"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                tokens.add(token.toString());
                token.setLength(0);
            } else {
                token.append(ch);
            }
        }

        tokens.add(token.toString());
        return tokens.toArray(new String[0]);
    }

    private static FlatType parseFlatType(String str) {
        return switch (str.toLowerCase()) {
            case "two-room" -> FlatType.TWO_ROOM;
            case "three-room" -> FlatType.THREE_ROOM;
            default -> throw new IllegalArgumentException("Unknown flat type: " + str);
        };
    }

    private static LocalDate parseDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");
        return LocalDate.parse(str, formatter);
    }

    public static void storeProjects(String filePath) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,");
            writer.write("Type 2,Number of units for Type 2,Selling price for Type 2,");
            writer.write("Application opening date,Application closing date,Manager,Officer Slot,Officer\n");

            for (Project project : projects) {
                String projectName = project.getProjectName();
                String neighborhood = project.getNeighborhood();

                Map<FlatType, Integer> units = project.getTotalUnits();
                Map<FlatType, Double> prices = project.getSellingPrices();

                List<FlatType> types = new ArrayList<>(units.keySet());

                FlatType type1 = types.get(0);
                FlatType type2 = types.size() > 1 ? types.get(1) : type1;

                int type1Units = units.get(type1);
                double type1Price = prices.get(type1);

                int type2Units = units.get(type2);
                double type2Price = prices.get(type2);

                String openingDate = project.getApplicationOpeningDate().format(DateTimeFormatter.ofPattern("d/M/yy"));
                String closingDate = project.getApplicationClosingDate().format(DateTimeFormatter.ofPattern("d/M/yy"));

                String managerName = project.getManager().getName();
                int officerSlots = project.getMaxOfficerSlots();

                // Join officer names with commas and wrap with quotes
                String officerNames = "\"" + String.join(",", project.getOfficerNames()) + "\"";

                // Write line
                writer.write(String.join(",", Arrays.asList(
                        projectName,
                        neighborhood,
                        toCSVFlatType(type1),
                        String.valueOf(type1Units),
                        String.valueOf(type1Price),
                        toCSVFlatType(type2),
                        String.valueOf(type2Units),
                        String.valueOf(type2Price),
                        openingDate,
                        closingDate,
                        managerName,
                        String.valueOf(officerSlots),
                        officerNames)) + "\n");
            }
        }
    }

    private static String toCSVFlatType(FlatType type) {
        return switch (type) {
            case TWO_ROOM -> "two-room";
            case THREE_ROOM -> "three-room";
            default -> type.name().toLowerCase();
        };
    }

}
