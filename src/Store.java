
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;


import java.io.*;
import java.util.List;

public class Store {
    private String name;
    private Currency currency;
    private Currency[] currencyList;
    private Product[] products;
    private Manufacturer[] manufacturers;
    private Discount[] discounts;
    private static Store instance;

    public String getStoreCurrencySymbol(){
        return currency.getSymbol();
    }

    public Store() {
        currencyList = new Currency[1];
    }

    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();

            instance.currency = instance.createCurrency("EUR", "€", 1.0);

        }
        return instance;
    }


    public void readCSV(String filename) throws FileNotFoundException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).build()) {
            List<String[]> r = reader.readAll();

            for (String[] x : r) {

                /** Verificam daca pretul este completat*/
                if (x[3].length() == 0) {
                    continue;
                }

                /** Creem noul producator si il adaugam la vectorul de producatori*/
                Manufacturer newManufacturer = new Manufacturer(x[2]);
                addManufacturer(newManufacturer);

                int quantity = 0;
                if (x[4].length() != 0) {
                    quantity = Integer.parseInt(x[4].replaceAll("\\D+", ""));
                }
                /** Creem noul produs si il adaugam la vectorul de produse*/
                Product newProduct = new ProductBuilder()
                        .withUniqueId(x[0])
                        .withName(x[1])
                        .withManufacturer(newManufacturer)
                        .withPrice(stringToPrice(x[3]))
                        .withQuantity(quantity)
                        .build();

                addProduct(newProduct);

            }

        } catch (IOException | CsvException | DuplicateProductException | DuplicateManufacturerException e) {
            e.printStackTrace();
        }

    }

    public void writeCSV(String fileName){
        File file = new File(fileName);
        try{
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            String[] headLine = {"uniq_id","product_name","manufacturer","price","number_available_in_stock"};
            writer.writeNext(headLine);

            for(Product x : products){
                writer.writeNext(x.toSave());
            }

            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addProduct(Product product) throws DuplicateProductException {
        try {
            if (products == null) {
                products = new Product[1];
                products[0] = product;

            } else {

                for (Product x : products) {
                    if (x.getUniqueId().equals(product.getUniqueId())) {
                        throw new DuplicateProductException();
                    }
                }
                Product[] aux = new Product[products.length + 1];
                System.arraycopy(products, 0, aux, 0, products.length);
                aux[products.length] = product;
                products = aux;
            }

        } catch (DuplicateProductException exception) {
            exception.printMessage();
        }

    }

    public void addManufacturer(Manufacturer manufacturer) throws DuplicateManufacturerException {
        try {
            if (manufacturers == null) {
                manufacturers = new Manufacturer[1];
                manufacturers[0] = manufacturer;

            } else {
                for (Manufacturer x : manufacturers) {
                    if (x.getName().equals(manufacturer.getName())) {
                        throw new DuplicateManufacturerException();
                    }
                }
                Manufacturer[] aux = new Manufacturer[manufacturers.length + 1];
                System.arraycopy(manufacturers, 0, aux, 0, manufacturers.length);
                aux[manufacturers.length] = manufacturer;
                manufacturers = aux;
            }
        } catch (DuplicateManufacturerException exception) {
            exception.printMessage();
        }
    }

    public void showStore() {
        for (Product x : products) {
            System.out.println(x.toString());
        }
    }

    public Currency createCurrency(String name, String symbol, double parityToEur) {
        Currency newCurrency = new Currency(name, symbol, parityToEur);
        if (currencyList[0] != null) {
            for (Currency x : currencyList) {
                if (x.getName().equals(name)) {
                    return null;
                }
            }

            Currency[] aux = new Currency[currencyList.length + 1];
            System.arraycopy(currencyList, 0, aux, 0, currencyList.length);
            aux[currencyList.length] = newCurrency;
            currencyList = aux;

        } else {
            currencyList[0] = newCurrency;
        }

        return newCurrency;
    }

    public void listCurrencies() {

        for (Currency x : currencyList) {
            System.out.println(x.toString());
        }
    }

    public String getStoreCurrency() {
        return currency.getName();
    }

    public void changeCurrency(Currency currency) throws CurrencyNotFoundException {
        if (currency.getName().equals(getInstance().currency.getName())) {
            return;
        }
        /** Verificam daca exista currency-ul cautat in lista*/
        for (Currency x : currencyList) {
            if (x.getName().equals(currency.getName())) {
                updatePrices(currency);
                this.currency = currency;
                return;
            }
        }

        throw new CurrencyNotFoundException();
    }

    public void updatePrices(Currency currency) {
        for (Product x : getInstance().products) {
            x.setPrice(x.getPrice() * getInstance().currency.getParityToEur());
            x.setPrice(x.getPrice() / currency.getParityToEur());
        }
    }

    public Discount createDiscount(DiscountType discountType, String name, double value) {
        Discount discount = new Discount(name, discountType, value);

        if (discounts == null) {
            discounts = new Discount[1];
            discounts[0] = discount;

        } else {

            Discount[] aux = new Discount[discounts.length + 1];
            System.arraycopy(discounts, 0, aux, 0, discounts.length);
            aux[discounts.length] = discount;
            discounts = aux;
        }

        return discount;
    }

    public static double stringToPrice(String price) {
        String symbol = Character.toString(price.charAt(0));
        String priceString = price.substring(1);
        double priceDouble = Double.parseDouble(priceString);
        if (!getInstance().currency.getSymbol().equals(symbol)) {
            for (Currency x : getInstance().currencyList) {
                if (x.getSymbol().equals(symbol)) {
                    priceDouble = priceDouble * x.getParityToEur();
                    priceDouble = priceDouble / getInstance().currency.getParityToEur();
                }
            }
        }
        return priceDouble;
    }

    public void setStoreCurrency(String name) {
        for (Currency x : getInstance().currencyList) {
            if (x.getName().equals(name)) {
                try {
                    getInstance().changeCurrency(x);
                } catch (CurrencyNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void updateParity(String name, double parityToEUR) {
        for (Currency x : getInstance().currencyList) {
            if (x.getName().equals(name)) {
                x.updateParity(parityToEUR);
                return;
            }
        }
    }

    public void showProduct(String id) {
        for (Product x : getInstance().products) {
            if (x.getUniqueId().equals(id)) {
                System.out.println(x.toString());
                return;
            }
        }

        System.out.println("Produsul nu exista in magazin");
    }

    public void listManufacturers() {
        for (Manufacturer x : getInstance().manufacturers) {
            System.out.println(x.getName());
        }
    }

    public void listProductsByManufacturarer(String name) {
        for (Product x : getInstance().products) {
            if (x.getManufacturer().getName().equals(name)) {
                System.out.println(x.toString());
            }
        }
    }

    public void listDiscounts() {
        if(getInstance().discounts == null){
            return;
        }
        for (Discount x : getInstance().discounts) {
            System.out.println(x.toString());
        }
    }

    public void applyDiscount(String name, double value) {
        if (name.equals("PERCENTAGE")) {
            for (Product x : getInstance().products) {
                x.setPrice(x.getPrice() * (100.0 - value) / 100.0);
            }
        } else if (name.equals("FIXED")) {
            for (Product x : getInstance().products) {
                x.setPrice(x.getPrice() - value);
            }
        }
    }

    public void calculateTotal(String[] products) {
        double total = 0;
        for (String s : products)
            for (Product x : getInstance().products) {
                if (s.equals(x.getUniqueId())) {
                    total += x.getPrice();
                }
            }
        System.out.println(getInstance().currency.getSymbol() + total);
    }
}
