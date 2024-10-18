package interfaces;
import models.entities.*;

import java.util.Scanner;

public interface Screen extends BaseScreen {
    void display(Scanner scanner, User user);
}

