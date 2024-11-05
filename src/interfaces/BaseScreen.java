package interfaces;
import java.util.*;

/**
 * Represents a base interface for screen functionality in the application.
 * Any screen in the application should implement this interface to ensure
 * a standard display method for user interaction.
 */
public interface BaseScreen {
    /**
     * Displays the screen to the user.
     *
     * @param scanner the Scanner object for capturing user input
     * This method can be overridden by implementing classes to provide specific display logic.
     */
    default void display(Scanner scanner) {

    }
}
