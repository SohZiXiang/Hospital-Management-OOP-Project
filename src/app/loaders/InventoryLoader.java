package app.loaders;
import interfaces.*;
import models.entities.Medicine;
import models.entities.Patient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryLoader implements DataLoader {
    @Override
    public List<Medicine> loadData(String filePath) {
        List<Medicine> MedicineList = new ArrayList<>();

        return MedicineList;
    }
}
