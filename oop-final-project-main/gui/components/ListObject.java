package gui.components;

import gui.structures.ListItem;

public class ListObject implements DisplayObject {
    private final int LINES_PER_PAGE = 5;
    private int currentIndex = 0;
    private int width;
    private String navigationMessage;
    private String initNavMessage;
    protected  ListItem[] lines;

    public ListObject(int width, String navigationMessage) {
        this.width = width;
        this.initNavMessage = navigationMessage;
        this.navigationMessage = initNavMessage;
    }

    public void setInitNavMessage(String navigationMessage){
        this.initNavMessage = navigationMessage;
        this.navigationMessage = initNavMessage;
    }

    public void setLines(ListItem[] lines) {
        this.lines = lines;
        if(lines != null){
            if (lines.length <= LINES_PER_PAGE) {
                navigationMessage = initNavMessage;
            } else {
                navigationMessage = "Use \"next\" to see next 5 items";
            }
        } else{
            navigationMessage = initNavMessage;
        }
    }

    public void printContent() {
        if (lines != null && lines.length != 0) {
            int endIndex = Math.min(currentIndex + LINES_PER_PAGE, lines.length);

            for (int i = currentIndex; i < endIndex; i++) {
                ListItem lineObj = lines[i];
                String color = lineObj.color != null ? lineObj.color : "\033[0m"; 
                String text = lineObj.text;

                // ensure padding
                String paddedLine = " ".repeat(1) + color + text + "\033[0m"
                        + " ".repeat(Math.max(0, this.width - 2 - text.length() - 1));

                System.out.println("│" + paddedLine + "│");
            }

            System.out.println("│" + "─".repeat(this.width - 2) + "│");
            System.out.println("│ " + navigationMessage + " ".repeat(this.width - 2 - navigationMessage.length() - 1) + "│");

        } else {
            System.out.println("│" + " ".repeat(1) + "Nothing here!" + " ".repeat(this.width - 16) + "│");
            System.out.println("│" + "─".repeat(this.width - 2) + "│");
            System.out.println("│ " + navigationMessage + " ".repeat(this.width - 2 - navigationMessage.length() - 1) + "│");
        }
    }
    public void moveNext() {
        // Advance only if not already on the last page
        if (currentIndex + LINES_PER_PAGE < lines.length) {
            currentIndex += LINES_PER_PAGE;
        }

        updateNavigationMessage();
    }

    public void movePrev() {
        if (currentIndex - LINES_PER_PAGE >= 0) {
            currentIndex -= LINES_PER_PAGE;
        }
    
        updateNavigationMessage();
    }
    
    private void updateNavigationMessage() {
        int totalPages = (int) Math.ceil((double) lines.length / LINES_PER_PAGE);
        int currentPage = currentIndex / LINES_PER_PAGE;

        if (totalPages <= 1) {
            navigationMessage = initNavMessage;
        } else if (currentPage == 0) {
            navigationMessage = "Use \"next\" to see next 5 items";
        } else if (currentPage == totalPages - 1) {
            navigationMessage = "Use \"prev\" to see previous 5 items";
        } else {
            navigationMessage = "Use \"next\" and \"prev\" to toggle items";
        }
    }

    public void updateIndex(String command) {
        if (command.trim().equalsIgnoreCase("next")) {
            this.moveNext();
        } else if (command.trim().equalsIgnoreCase("prev")) {
            this.movePrev();
        }
    }
}
