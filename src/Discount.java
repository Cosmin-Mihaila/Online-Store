import java.time.LocalDateTime;

public class Discount {
    private String name;
    private DiscountType discountType;
    private double value;
    private LocalDateTime lastDateApplied;

    public DiscountType getDiscountType() {
        return discountType;
    }

    public double getValue() {
        return value;
    }

    public Discount(DiscountType discountType, double value){
        this.discountType = discountType;
        this.value = value;
    }

    public Discount(String name, DiscountType discountType, double value) {
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        lastDateApplied = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Discount{" +
                "name='" + name + '\'' +
                ", discountType=" + discountType +
                ", value=" + value +
                ", lastDateApplied=" + lastDateApplied +
                '}';
    }

}
