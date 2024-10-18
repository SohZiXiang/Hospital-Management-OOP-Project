package interfaces;
import models.entities.*;

import java.util.Scanner;

public interface Screen {
    void display(Scanner scanner);
    void display(Scanner scanner, User user);
}

