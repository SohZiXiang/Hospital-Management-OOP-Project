package app.screens;
import app.Main;
import app.screens.patient.UpdatePersonalInformationScreen;
import app.screens.patient.ViewMedicalRecord;
import app.screens.patient.ViewPatientAppointmentScreen;
import interfaces.Screen;
import models.entities.User;
import java.util.Scanner;

public class PatientScreen implements Screen {
    public void display(Scanner scanner, User user) {

        while (true) {
            System.out.println();
            System.out.println("|\\   __  \\|\\   __  \\|\\___   ___\\\\  \\|\\  ___ \\ |\\   ___  \\|\\___   ___\\ \n" +
                    "\\ \\  \\|\\  \\ \\  \\|\\  \\|___ \\  \\_\\ \\  \\ \\   __/|\\ \\  \\\\ \\  \\|___ \\  \\_| \n" +
                    " \\ \\   ____\\ \\   __  \\   \\ \\  \\ \\ \\  \\ \\  \\_|/_\\ \\  \\\\ \\  \\   \\ \\  \\  \n" +
                    "  \\ \\  \\___|\\ \\  \\ \\  \\   \\ \\  \\ \\ \\  \\ \\  \\_|\\ \\ \\  \\\\ \\  \\   \\ \\  \\ \n" +
                    "   \\ \\__\\    \\ \\__\\ \\__\\   \\ \\__\\ \\ \\__\\ \\_______\\ \\__\\\\ \\__\\   \\ \\__\\\n" +
                    "    \\|__|     \\|__|\\|__|    \\|__|  \\|__|\\|_______|\\|__| \\|__|    \\|__|");
            System.out.println("\n--- Patient Menu---");
            System.out.println("1. View Medical Records");
            System.out.println("2. Update Personal Information");
            System.out.println("3. View Available Appointment Slots");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Reschedule an Appointment");
            System.out.println("6. Cancel an Appointment");
            System.out.println("7. View Scheduled Appointments");
            System.out.println("8. View Past Appointment Outcome Records");
            System.out.println("9. Logout");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        ViewMedicalRecord viewMedicalRecord = new ViewMedicalRecord();
                        viewMedicalRecord.display(scanner,user);
                        break;
                    case 2:
                        UpdatePersonalInformationScreen updatePersonalInformationScreen = new UpdatePersonalInformationScreen();
                        updatePersonalInformationScreen.display(scanner,user);
                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:
                        ViewPatientAppointmentScreen viewPatientAppointmentScreen = new ViewPatientAppointmentScreen();
                        viewPatientAppointmentScreen.display(scanner,user);
                        break;
                    case 8:

                        break;
                    case 9:
                        System.out.println("Logging out...");
                        Main.displayMain(scanner);
                        break;

                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
