package models.entities;

/**
 * The Medicine class represents a medicine in the system, including its name, stock quantity,
 * and a low-stock alert threshold.
 */
public class Medicine {
    private String name;
    private int stock;
    private int lowStockAlert;

    /**
     * Constructs a Medicine object with the specified name, stock quantity, and low-stock alert threshold.
     *
     * @param name          The name of the medicine.
     * @param stock         The initial stock quantity of the medicine.
     * @param lowStockAlert The stock quantity threshold for triggering a low-stock alert.
     */
    public Medicine(String name, int stock, int lowStockAlert) {
        this.name = name;
        this.stock = stock;
        this.lowStockAlert = lowStockAlert;
    }

    /**
     * Constructs a Medicine object with the specified name.
     * Stock quantity and low-stock alert threshold are not initialized in this constructor.
     *
     * @param name The name of the medicine.
     */
    public Medicine(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the medicine.
     *
     * @return The name of the medicine.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the medicine.
     *
     * @param name The new name of the medicine.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the current stock quantity of the medicine.
     *
     * @return The stock quantity.
     */
    public int getQuantity() {
        return stock;
    }

    /**
     * Sets the stock quantity of the medicine.
     *
     * @param stock The new stock quantity.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Gets the current stock quantity of the medicine.
     *
     * @return The stock quantity.
     */
    public int getStock() {return stock;}

    /**
     * Gets the low-stock alert threshold for the medicine.
     *
     * @return The low-stock alert threshold.
     */
    public int getLowStockAlert() {
        return lowStockAlert;
    }

    /**
     * Sets the low-stock alert threshold for the medicine.
     *
     * @param lowStockAlert The new low-stock alert threshold.
     */
    public void setLowStockAlert(int lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }
}
