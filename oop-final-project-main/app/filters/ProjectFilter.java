package app.filters;

public enum ProjectFilter {
    SELLING_PRICES_ASCENDING,
    SELLING_PRICES_DESCENDING,
    ONLY_OFFICER,
    ONLY_NEIGHBORHOOD,
    ONLY_MANAGER,
    ONLY_OPENING_AFTER,
    ONLY_UNITS_GREATER_THAN,
    ONLY_FLAT_TYPE,
    NONE;

    public static ProjectFilter fromCommand(String command) {
        String cmd = command.toLowerCase().trim();
        if (cmd.equals("none")) return NONE;
        if (cmd.startsWith("sellingprices ascending")) return SELLING_PRICES_ASCENDING;
        if (cmd.startsWith("sellingprices descending")) return SELLING_PRICES_DESCENDING;
        if (cmd.startsWith("only officer=")) return ONLY_OFFICER;
        if (cmd.startsWith("only neighborhood=")) return ONLY_NEIGHBORHOOD;
        if (cmd.startsWith("only manager=")) return ONLY_MANAGER;
        if (cmd.startsWith("only opening=after:")) return ONLY_OPENING_AFTER;
        if (cmd.startsWith("only units>")) return ONLY_UNITS_GREATER_THAN;
        if (cmd.startsWith("only flattype=")) return ONLY_FLAT_TYPE;

        throw new IllegalArgumentException("Invalid filter command: " + command);
    }
}
