package app.strategies;

import gui.UserInterface;

public enum MenuStrategyProvider {
    APPLICANT(new ApplicantStrategy()),
    HDB_OFFICER(new HDBOfficerStrategy()),
    HDB_MANAGER(new HDBManagerStrategy()),
    UNKNOWN(new UnknownMenuStrategy());

    private final MenuStrategy strategy;

    MenuStrategyProvider(MenuStrategy strategy) {
        this.strategy = strategy;
    }

    public void initialize(Integer width, UserInterface ui) {
        strategy.initialize(width, ui);
    }

    public void handleCommand(String input, Integer menuID, UserInterface ui) {
        strategy.handleCommand(input, menuID, ui);
    }

    public static MenuStrategyProvider fromRole(String role) {
        return switch (role.toLowerCase()) {
            case "applicant" -> APPLICANT;
            case "hdb officer" -> HDB_OFFICER;
            case "hdb manager" -> HDB_MANAGER;
            default -> UNKNOWN;
        };
    }
}
