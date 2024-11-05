package interfaces;
import models.entities.*;

import java.util.Scanner;

/**
 * Represents a screen in the application that can be displayed to the user.
 * This interface extends the BaseScreen interface and requires a specific
 * display method that takes a user as an argument. Usually used after user login for session tracking
 */
public interface Screen extends BaseScreen {

    /**
     * Displays the screen to the user, providing an interface for user interaction.
     *
     * @param scanner the Scanner object for capturing user input
     * @param user the User object representing the currently logged-in user
     */
    void display(Scanner scanner, User user);
}

