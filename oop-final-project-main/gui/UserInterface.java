package gui;

import gui.components.DisplayObject;

public class UserInterface {
    private int width;
    public DisplayObject object;

    private UserInterface(int width) {
        this.width = width;
    }

    private static class LazyLoader {
        private static UserInterface instance;
    }

    public static UserInterface getInstance(int width) {
        if (LazyLoader.instance == null) {
            LazyLoader.instance = new UserInterface(width);
        }
        return LazyLoader.instance;
    }

    public static UserInterface getInstance() {
        if (LazyLoader.instance == null) {
            throw new IllegalStateException("UserInterface must be initialized first.");
        }
        return LazyLoader.instance;
    }

    public void setObject(DisplayObject object) {
        this.object = object;
    }

    public Class<?> getObject() {
        if (this.object != null) {
            return this.object.getClass();
        }
        return null;
    }

    public DisplayObject getUIObject(){
        return object;
    }

    public void freshUI(String command) {
        if (object != null) {
            object.updateIndex(command);
            String horizontalBorder = "┌" + "─".repeat(this.width - 2) + "┐";
            System.out.println(horizontalBorder);
            object.printContent();
            System.out.println(horizontalBorder.replace('┌', '└').replace('┐', '┘') + "\n");
        } else {
            System.out.println("Nothing to render.\n");
        }
    }
}
