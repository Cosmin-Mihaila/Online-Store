import java.io.Serializable;

public class Product implements Serializable {
    private String uniqueId;
    private String name;
    private Manufacturer manufacturer;
    private double price;
    private int quantity;
    private Discount discount;
    public Product(){}

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "uniqueId='" + uniqueId + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer=" + manufacturer.getName() +
                ", price=" + price +
                ", quantity=" + quantity +
                ", discount=" + discount +
                '}';
    }

    /**
     * Metoda pentru a returna detaliile produsului intr-un String[] pentru a putea fi
     * scris in fisierul CSV
     * @return Vectorul de String ce reprezinta detaliile produslui
     */
    public String[] toSave(){
        String[] aux = new String[5];
        aux[0] = uniqueId;
        aux[1] = name;
        aux[2] = manufacturer.getName();
        aux[3] = Store.getInstance().getStoreCurrencySymbol() + price;
        aux[4] = Integer.toString(quantity);

        return aux;
    }
}
