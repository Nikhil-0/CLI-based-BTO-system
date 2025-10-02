package app.filters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import appmodules.applicant.FlatType;
import main.MainMenu;
import models.Project;

public class ProjectFilterUtils {

    public static List<Project> filterProjects(List<Project> projects, String command) {
        ProjectFilter filter = ProjectFilter.fromCommand(command);

        return switch (filter) {
            case SELLING_PRICES_ASCENDING -> sortBySellingPrices(projects, true);
            case SELLING_PRICES_DESCENDING -> sortBySellingPrices(projects, false);
            case ONLY_OFFICER -> filterByOfficer(projects, extractValue(command));
            case ONLY_NEIGHBORHOOD -> filterByNeighborhood(projects, extractValue(command));
            case ONLY_MANAGER -> filterByManager(projects, extractValue(command));
            case ONLY_OPENING_AFTER -> filterByOpeningDate(projects, extractValueAfterColon(command));
            case ONLY_UNITS_GREATER_THAN -> filterByTotalUnits(projects, Integer.parseInt(extractValue(command).replace(">", "").trim()));
            case ONLY_FLAT_TYPE -> filterByFlatType(projects, extractValue(command));
            case NONE -> projects;
        };
    }

    private static String extractValue(String command) {
        int equalsIndex = command.indexOf('=');
        if (equalsIndex == -1 || equalsIndex == command.length() - 1) {
            MainMenu.errorMsg = "Missing or invalid filter value in: " + command;
        }
        return command.substring(equalsIndex + 1).trim();
    }

    private static String extractValueAfterColon(String command) {
        int colonIndex = command.indexOf(':');
        if (colonIndex == -1 || colonIndex == command.length() - 1) {
            MainMenu.errorMsg = "Missing date after colon in: " + command;
        }
        return command.substring(colonIndex + 1).trim();
    }

    private static List<Project> sortBySellingPrices(List<Project> projects, boolean ascending) {
        Comparator<Project> comparator = Comparator.comparingDouble(ProjectFilterUtils::averageSellingPrice);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        return projects.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private static double averageSellingPrice(Project project) {
        Map<FlatType, Double> prices = project.getSellingPrices();
        if (prices.isEmpty()) return 0.0;

        return prices.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private static List<Project> filterByOfficer(List<Project> projects, String value) {
        Set<String> officers = Arrays.stream(value.split(","))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return projects.stream()
                .filter(p -> p.getOfficerNames().stream().anyMatch(o -> officers.contains(o.toLowerCase())))
                .collect(Collectors.toList());
    }

    private static List<Project> filterByNeighborhood(List<Project> projects, String value) {
        return projects.stream()
                .filter(p -> p.getNeighborhood().equalsIgnoreCase(value))
                .collect(Collectors.toList());
    }

    private static List<Project> filterByManager(List<Project> projects, String value) {
        return projects.stream()
                .filter(p -> p.getManager().getName().equalsIgnoreCase(value))
                .collect(Collectors.toList());
    }

    private static List<Project> filterByOpeningDate(List<Project> projects, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d/M/yy"));
        return projects.stream()
                .filter(p -> !p.getApplicationOpeningDate().isBefore(date))
                .collect(Collectors.toList());
    }

    private static List<Project> filterByTotalUnits(List<Project> projects, int threshold) {
        return projects.stream()
                .filter(p -> p.getTotalUnits().values().stream().mapToInt(Integer::intValue).sum() > threshold)
                .collect(Collectors.toList());
    }

    private static List<Project> filterByFlatType(List<Project> projects, String flatTypeStr) {
        FlatType type = FlatType.valueOf(flatTypeStr.toUpperCase().replace("-", "_"));
        return projects.stream()
                .filter(p -> p.getTotalUnits().containsKey(type))
                .collect(Collectors.toList());
    }
}
