package app.guimanagers;

import java.util.ArrayList;
import java.util.List;

import app.loaders.LoadCSV;
import gui.UserInterface;
import gui.components.BoxObject;
import gui.components.ListObject;
import gui.structures.Field;
import gui.structures.ListItem;
import main.MainMenu;
import models.User;

public class AuthManager {
    public static Integer width;
    public static UserInterface ui;

    public static List<User> users = new ArrayList<>();

    private static ListObject currOptionsList;
    private static BoxObject currLoginBox, currRegisterBox;
    private static Boolean isLoggingIn = false;
    private static Boolean isRegistering = false;

    private static User currRegistrant, currUser;

    public static void initialize(int widthVal, UserInterface uiRef) {
        ui = uiRef;
        width = widthVal;
        currLoginBox = new BoxObject(width, "App Log In", "Command: '[FIELD NAME] [YOUR command]'");
        currRegisterBox = new BoxObject(width, "Register", "Command: '[FIELD NAME] [YOUR command]'");
        currOptionsList = new ListObject(width, "Please select a login option.");
        currOptionsList.setLines(new ListItem[] {
                new ListItem("Register", null),
                new ListItem("Login", null)
        });

    }

    public static void applyMenu() {
        if (isRegistering) {
            ui.setObject(currRegisterBox);
        } else if (isLoggingIn) {
            ui.setObject(currLoginBox);
        } else {
            ui.setObject(currOptionsList);
        }
    }

    public static void changePassword(String newPassword){
        for (User user : AuthManager.users) {
            if (user.getNric().equals(MainMenu.activeUser.getNric())) {
                user.setPassword(newPassword);
                break;
            }
        }
    }

    private static boolean isCommand(String command) {
        return command.matches("(?i)(Login|Register|NRIC|Password|Hide|Reveal|Confirm|Marital|Age|Role|Name|Changepassword)(\\s+.*)?");
    }

    private static void validatecommand(String command) {
        if (!isCommand(command)) {
            MainMenu.errorMsg = "Incorrect command. Please try again.";
        }
    }

    public static void processCommand(String command) {
        validatecommand(command);
        if (command.equalsIgnoreCase("register") && !isLoggingIn && !isRegistering) {
            currRegistrant = new User(null, null, null, null, null, null);
            isLoggingIn = false;
            isRegistering = true;
            setRegisterFields(false);
        } else if (command.equalsIgnoreCase("login") && !isLoggingIn && !isRegistering) {
            currUser = new User(null, null, null, null, null, null);
            isRegistering = false;
            isLoggingIn = true;
            setLoginFields(false);
        } else if (command.equalsIgnoreCase("exit")) {
            if (isRegistering || isLoggingIn) {
                isRegistering = false;
                isLoggingIn = false;
            }
        } else if (command.toLowerCase().startsWith("name ") && isRegistering) {
            currRegistrant.setName(command.substring(5).trim());
            setRegisterFields(false);
        } else if (command.toLowerCase().startsWith("nric ")) {
            String nric = command.substring(5);
            if (validateNRIC(nric)) {
                if (isRegistering) {
                    currRegistrant.setNric(nric.trim().toUpperCase());
                    setRegisterFields(false);
                } else if (isLoggingIn) {
                    currUser.setNric(nric.trim().toUpperCase());
                    setLoginFields(false);
                }
            }

        } else if (command.toLowerCase().startsWith("password ")) {
            String password = command.substring(9);

            if (isRegistering) {
                if (validatePassword(password)) {
                    currRegistrant.setPassword(password.trim());
                    setRegisterFields(false);
                }

            } else if (isLoggingIn) {
                currUser.setPassword(password.trim());
                setLoginFields(false);
            }

        } else if (command.equalsIgnoreCase("reveal"))

        {
            if (isRegistering) {
                setRegisterFields(true);
            } else if (isLoggingIn) {
                setLoginFields(true);
            }
        } else if (command.equalsIgnoreCase("hide")) {
            if (isRegistering) {
                setRegisterFields(false);
            } else if (isLoggingIn) {
                setLoginFields(false);
            }
        } else if (command.startsWith("marital ") && isRegistering) {
            currRegistrant.setMaritalStatus(command.substring(8));
            setRegisterFields(false);
        } else if (command.startsWith("role ") && isRegistering) {
            currRegistrant.setRole(command.substring(5));
            setRegisterFields(false);
        } else if (command.startsWith("age ") && isRegistering) {
            String age = command.substring(4).trim();
            currRegistrant.setAge(age.isEmpty() ? 0 : Integer.parseInt(age));
            setRegisterFields(false);
        } else if (command.equalsIgnoreCase("confirm")) {
            if (isRegistering) {
                boolean hasEmptyField = currRegistrant.getMaritalStatus() == null
                        || currRegistrant.getMaritalStatus().equals("Unknown") ||
                        currRegistrant.getAge() == 0 ||
                        currRegistrant.getPassword() == null || currRegistrant.getPassword().isEmpty() ||
                        currRegistrant.getNric() == null || currRegistrant.getNric().isEmpty() ||
                        currRegistrant.getRole() == null || currRegistrant.getRole().equals("Unknown");

                if (hasEmptyField) {
                    MainMenu.errorMsg = "Required field(s) empty.";
                } else {
                    users.add(currRegistrant);
                    LoadCSV.updateCSV(AuthManager.users);
                    MainMenu.errorMsg = "User successfully registered!";
                    isRegistering = false;
                    isLoggingIn = false;
                }

            } else if (isLoggingIn) {
                boolean found = false;
                for (User user : users) {
                    if (user.getNric() != null && user.getNric().equals(currUser.getNric())) {
                        found = true;
                        if (user.getPassword() != null && user.getPassword().equals(currUser.getPassword())) {
                            currUser = user;
                            isLoggingIn = false;
                            MainMenu.activeUser = currUser;
                            MainMenu.loggedIn = true;
                        } else {
                            MainMenu.errorMsg = "Incorrect password.";
                        }
                        break;
                    }
                }

                if (!found) {
                    MainMenu.errorMsg = "NRIC not found. Please register first.";
                }
            }
        }

    }

    private static Boolean validateNRIC(String nric) {
        if (nric == null || nric.trim().isEmpty()) {
            MainMenu.errorMsg = "NRIC cannot be null or empty.";
            return false;
        }
        String correctedNric = nric.trim().toUpperCase();
        if (!correctedNric.matches("^[STFG]\\d{7}[A-Z]$")) {
            MainMenu.errorMsg = "Invalid NRIC format. Example: S1234567D";
            return false;
        }

        return true;
    }

    public static Boolean validatePassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            MainMenu.errorMsg = "Password must be at least 8 characters long.";
            return false;
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|`~].*");

        if (!hasUpper) {
            MainMenu.errorMsg = "Password must contain at least one uppercase letter.";
            return false;
        }

        if (!hasLower) {
            MainMenu.errorMsg = "Password must contain at least one lowercase letter.";
            return false;
        }

        if (!hasDigit) {
            MainMenu.errorMsg = "Password must contain at least one digit.";
            return false;
        }

        if (!hasSpecial) {
            MainMenu.errorMsg = "Password must contain at least one special character.";
            return false;
        }

        return true;
    }

    private static void setLoginFields(Boolean viewPassword) {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("NRIC", currUser.getNric()));
        if (viewPassword) {
            parsedFields.add(new Field("Password", currUser.getPassword()));
        } else {
            parsedFields.add(new Field("Password", "*".repeat(currUser.getPassword().length())));
        }
        currLoginBox.initialiseFields(parsedFields);
    }

    private static void setRegisterFields(Boolean viewPassword) {
        List<Field> parsedFields = new ArrayList<>();
        parsedFields.add(new Field("Name", currRegistrant.getName()));
        parsedFields.add(new Field("NRIC", currRegistrant.getNric()));
        if (viewPassword) {
            parsedFields.add(new Field("Password", currRegistrant.getPassword()));
        } else {
            parsedFields.add(new Field("Password", "*".repeat(currRegistrant.getPassword().length())));
        }
        parsedFields.add(new Field("Marital Status", currRegistrant.getMaritalStatus()));
        parsedFields.add(new Field("Age", String.valueOf(currRegistrant.getAge())));
        parsedFields.add(new Field("Role", String.valueOf(currRegistrant.getRole())));
        currRegisterBox.initialiseFields(parsedFields);
    }

}
