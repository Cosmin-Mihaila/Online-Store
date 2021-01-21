public class Manufacturer {
    private String name;
    private int countProducts;

    Manufacturer(String name){
        this.name = name;
        this.countProducts = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountProducts() {
        return countProducts;
    }

    public void setCountProducts(int countProducts) {
        this.countProducts = countProducts;
    }

    public void increaseCountProducts(){
        this.countProducts ++;
    }
}
