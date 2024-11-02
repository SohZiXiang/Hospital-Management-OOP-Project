package models.entities;

public class Medicine {
    private String name;
    private int stock;
    private int lowStockAlert;
    private int qtyRequested;
    public Medicine(String name, int stock, int lowStockAlert) {
        this.name = name;
        this.stock = stock;
        this.lowStockAlert = lowStockAlert;
    }

    public Medicine(String name, int qtyRequested) {
        this.name = name;
        this.qtyRequested = qtyRequested;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getQuantity() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getStock() {return stock;}
    public int getLowStockAlert() {
        return lowStockAlert;
    }
    public void setLowStockAlert(int lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }
    public int getQtyRequested() { return qtyRequested; }
}
