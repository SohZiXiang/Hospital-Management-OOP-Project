package models.entities;

import java.util.List;
import java.util.*;

public class InventoryManager {
    private List<Medicine> medicines;
    public InventoryManager() {
        this.medicines = new ArrayList<>(
        );
    }
    public void addMedicine(Medicine medicine) {
        this.medicines.add(medicine);
    }

    public void updateMedicineStock(String medicineName, int newStock) {
        for(Medicine medicine:medicines){
            if(medicine.getName().equals(medicineName)){
                medicine.setStock(newStock);
                break;
            }
        }
    }

    public List<Medicine> getMedicines() {
        return this.medicines;
    }


}
