package gui;
public class Terminal {
    public static int getTerminalWidth(String columns) {
        if (columns != null) {
            try {
                return Integer.parseInt(columns);
            } catch (NumberFormatException e) {
                return 80;
            }
        }
        return 80;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.print("\033[3J");
        System.out.flush();
    }
}
