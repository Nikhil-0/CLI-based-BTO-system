package gui.components;

import java.util.ArrayList;
import java.util.List;

import gui.structures.Field;

public class BoxObject implements DisplayObject {
    private int width;
    protected String instruction;
    List<Field> fields = new ArrayList<>();
    private String title;

    public BoxObject(int width, String title, String instruction) {
        this.width = width;
        this.instruction = instruction;
        this.title = title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    @Override
    public void printContent() {
        String trimmedTitle = truncateWithEllipsis(title, this.width - 3);
        System.out.println("│" + " ".repeat(1) + "\033[1m" + trimmedTitle + "\033[0m"
                + " ".repeat(this.width - 3 - trimmedTitle.length()) + "│");
    
        for (int i = 0; i < fields.size(); i++) {
            String key = fields.get(i).getFieldKey();
            String value = fields.get(i).getFieldValue();
    
            String displayKey = (key == null || key.isBlank()) ? "\u001B[90mEmpty field\u001B[0m" : key;
            String displayValue = (value == null || value.isBlank()) ? "\u001B[90mEmpty value\u001B[0m" : value;
    
            String line = displayKey + ": " + displayValue;
            printWrappedText(line);
        }
    
        System.out.println("│" + "─".repeat(this.width - 2) + "│");
        String trimmedInstruction = truncateWithEllipsis(instruction, this.width - 3);
        System.out.println("│ " + trimmedInstruction + " ".repeat(this.width - 3 - trimmedInstruction.length()) + "│");
    }
    
    private void printWrappedText(String text) {
        int contentWidth = this.width - 3;
    
        int index = 0;
        while (index < text.length()) {
            int len = 0;
            int i = index;
            while (i < text.length() && len < contentWidth) {
                char c = text.charAt(i);
                if (c == '\u001B') { // Start of ANSI sequence
                    while (i < text.length() && text.charAt(i) != 'm') {
                        i++;
                    }
                    i++; // include 'm'
                } else {
                    len++;
                    i++;
                }
            }
    
            String part = text.substring(index, i);
            int visibleLength = getVisibleLength(part);
            String padded = " ".repeat(1) + part + " ".repeat(contentWidth - visibleLength);
            System.out.println("│" + padded + "│");
    
            index = i;
        }
    }
    
    private int getVisibleLength(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    private String truncateWithEllipsis(String text, int maxLength) {
        if (maxLength <= 3) {
            return "...".substring(0, Math.max(0, maxLength)); // handle edge case safely
        }
    
        if (text.length() <= maxLength) {
            return text;
        }
    
        return text.substring(0, maxLength - 3) + "...";
    }
    

    public void updateIndex(String command) {
    }

    public void initialiseFields(List<Field> fieldList) {
        this.fields = fieldList;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public void setThisValue(String fieldName, String value) {
        for (Field field : fields) {
            if (field.getFieldKey().equals(fieldName)) {
                field.setFieldValue(value);
                return;
            }
        }
    }
}