
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;

public class Store implements Serializable {
    private String name;
    private Currency currency;
    private Currency[] currencyList;
    private Product[] products;
    private Manufacturer[] manufacturers;
    private Discount[] discounts;
    private static Store instance = null;

    public String getStoreCurrencySymbol() {
        return currency.getSymbol();
    }

    public Store() {
        currencyList = new Currency[1];
    }

    /**
     * Metoda getInstance pentru design pattern-ul Singleton
     *
     * @return Instanta clasei
     */
    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();
            instance.currency = instance.createCurrency("EUR", "€", 1.0);

        }
        return instance;
    }

    /**
     * Metoda ce citeste produsele dintr-un fisier CSV si le pune in vectorul de produse
     *
     * @param filename Numele fisierului din care se citeste CSV-ul
     * @throws FileNotFoundException Exceptie daca nu se gaseste fisierul respectiv
     */
    public void readCSV(String filename) throws FileNotFoundException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).build()) {
            List<String[]> r = reader.readAll();

            for (String[] x : r) {

                /* Verificam daca pretul este completat*/
                if (x[3].length() == 0) {
                    continue;
                }

                /* Creem noul producator si il adaugam la vectorul de producatori*/
                Manufacturer newManufacturer = new Manufacturer(x[2]);

                try {
                    addManufacturer(newManufacturer);
                } catch (DuplicateManufacturerException exception) {
                    exception.printMessage();
                }

                int quantity = 0;
                if (x[4].length() != 0) {
                    quantity = Integer.parseInt(x[4].replaceAll("\\D+", ""));
                }

                /* Creem noul produs si il adaugam la vectorul de produse*/
                Product newProduct = new ProductBuilder()
                        .withUniqueId(x[0])
                        .withName(x[1])
                        .withManufacturer(newManufacturer)
                        .withPrice(stringToPrice(x[3]))
                        .withQuantity(quantity)
                        .build();
                try {
                    addProduct(newProduct);
                } catch (DuplicateProductException exception) {
                    exception.printMessage();
                }

            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoda ce scrie produsele si detaliile acestora intr-un fisier CSV
     *
     * @param fileName Numele fisierului in care se va scrie CSV-ul
     */
    public void writeCSV(String fileName) {
        File file = new File(fileName);
        try {
            FileWriter outputfile = new FileWriter(file);

            /* Creem obiectul CSVWriter*/
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            /* Declaram si scriem prima linie, ce reprezinta numele coloanelor*/
            String[] headLine = {"uniq_id", "product_name", "manufacturer", "price", "number_available_in_stock"};
            writer.writeNext(headLine);

            /* Luam fiecare produs si il scriem in fisierul CSV*/
            for (Product x : products) {
                writer.writeNext(x.toSave());
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda ce adauga in produs in vectorul de produse
     *
     * @param product Produsul ce trebuie adaugat in vector
     * @throws DuplicateProductException Exceptie in cazul in care produsul exista deja in vector
     */
    public void addProduct(Product product) throws DuplicateProductException {

        /*Verificam daca vectorul de produse este gol*/
        if (products == null) {
            products = new Product[1];
            products[0] = product;

        } else {

            /* Verificam daca mai exista un produs cu acelasi id*/

            for (Product x : products) {
                if (x.getUniqueId().equals(product.getUniqueId())) {
                    throw new DuplicateProductException();
                }
            }

            /*Marim dimensiunea vectorului si adaugam noul produs*/

            Product[] aux = new Product[products.length + 1];
            System.arraycopy(products, 0, aux, 0, products.length);
            aux[products.length] = product;
            products = aux;
        }


    }

    /**
     * Metoda ce adauga un nou producator la vectorul de producatori
     *
     * @param manufacturer Producatorul ce trebuie adaugat
     * @throws DuplicateManufacturerException Excpetie in cazul in care producatorul exista deja in vector
     */
    public void addManufacturer(Manufacturer manufacturer) throws DuplicateManufacturerException {

        /* Verificam daca vectorul de producatori este gol*/
        if (manufacturers == null) {
            manufacturers = new Manufacturer[1];
            manufacturers[0] = manufacturer;

        } else {

            /* Verificam daca producatorul exista deja in vectorul de producatori*/
            for (Manufacturer x : manufacturers) {
                if (x.getName().equals(manufacturer.getName())) {
                    x.increaseCountProducts();
                    throw new DuplicateManufacturerException();
                }
            }

            /* Marim dimensiunea vectorului si adaugam noul producator*/
            Manufacturer[] aux = new Manufacturer[manufacturers.length + 1];
            System.arraycopy(manufacturers, 0, aux, 0, manufacturers.length);
            aux[manufacturers.length] = manufacturer;
            manufacturers = aux;
        }

    }

    /**
     * Metoda ce parcurge tot vectorul de produse si afiseaza detalii despre fiecare produs
     */
    public void showStore() {
        if (products == null) return;
        for (Product x : products) {
            System.out.println(x.toString());
        }
    }

    /**
     * Metoda ce creeaza un nou currency si il adauga la lista de currency-uri
     *
     * @param name        Numele currency-ului creat
     * @param symbol      Simbolul currency-ului creat
     * @param parityToEur Paritatea fata de EUR a currency-ului creat
     * @return Returneaza noul currency
     */
    public Currency createCurrency(String name, String symbol, double parityToEur) {
        Currency newCurrency = new Currency(name, symbol, parityToEur);

        /* Parcurge toate currency-urile si verifica daca currency-ul pe care */
        if (currencyList[0] != null) {
            for (Currency x : currencyList) {
                if (x.getName().equals(name)) {
                    return null;
                }
            }
            /* Marim dimensiunea vectorului si adaugam noul currency*/
            Currency[] aux = new Currency[currencyList.length + 1];
            System.arraycopy(currencyList, 0, aux, 0, currencyList.length);
            aux[currencyList.length] = newCurrency;
            currencyList = aux;

        } else {
            currencyList[0] = newCurrency;
        }

        return newCurrency;
    }

    /**
     * Metoda ce afiseaza toate currency-ului de pe store
     */
    public void listCurrencies() {
        for (Currency x : currencyList) {
            System.out.println(x.toString());
        }
    }

    /**
     * Metoda ce returneaza currency-ul curent de pe store
     *
     * @return Currency-ul curent
     */
    public String getStoreCurrency() {
        return currency.getName();
    }

    /**
     * Metoda ce schimba currency-ul curent de pe store si actualizeaza preturile in noul currency
     *
     * @param currency Noul currency cu care este este schimbat cel actual
     * @throws CurrencyNotFoundException Exceptie in cazul in care currency-ul nu se afla pe store
     */
    public void changeCurrency(Currency currency) throws CurrencyNotFoundException {
        if (currency.getName().equals(getInstance().currency.getName())) {
            return;
        }

        /* Verificam daca exista currency-ul cautat in lista*/
        for (Currency x : currencyList) {
            if (x.getName().equals(currency.getName())) {
                updatePrices(currency);
                this.currency = currency;
                return;
            }
        }

        throw new CurrencyNotFoundException();
    }

    /**
     * Metoda ce updateaza preturile produselor in functie de currency-ul primit
     *
     * @param currency Currency-ul dupa care se updateaza preturile
     */
    public void updatePrices(Currency currency) {
        for (Product x : getInstance().products) {
            x.setPrice(x.getPrice() * getInstance().currency.getParityToEur());
            x.setPrice(x.getPrice() / currency.getParityToEur());
        }
    }

    /**
     * Metoda ce creeaza un Discount si il adauga la vectorul de discounturi
     *
     * @param discountType Tipul discountului
     * @param name         Numele discountului
     * @param value        Valoarea discountului
     * @return Discountul creat si returnat
     */
    public Discount createDiscount(DiscountType discountType, String name, double value) {
        Discount discount = new Discount(name, discountType, value);

        /* Verificam daca vectorul de discounturi este null*/
        if (discounts == null) {
            discounts = new Discount[1];
            discounts[0] = discount;

        } else {

            /* Marim dimensiunea vectorului si adaugam noul discount*/
            Discount[] aux = new Discount[discounts.length + 1];
            System.arraycopy(discounts, 0, aux, 0, discounts.length);
            aux[discounts.length] = discount;
            discounts = aux;
        }

        return discount;
    }

    /**
     * Metoda ce primeste un string in care este inclus pretul si currency-ul si returneaza
     * pretul in currency-ul de pe store
     *
     * @param price String-ul ce include pretul si currency-ul
     * @return Pretul in currency-ul de pe store
     */
    public static double stringToPrice(String price) {
        String symbol = Character.toString(price.charAt(0));
        String priceString = price.substring(1);
        double priceDouble = Double.parseDouble(priceString);

        /* Verificam daca currency-ul preturului este diferit de cel de pe store*/
        if (!getInstance().currency.getSymbol().equals(symbol)) {

            /* Cautam currency-ul in lista de currency-uri de pe store*/
            for (Currency x : getInstance().currencyList) {

                /* Modificam pretul primit in functie de paritatea currency-ului la EUR*/
                if (x.getSymbol().equals(symbol)) {
                    priceDouble = priceDouble * x.getParityToEur();
                    priceDouble = priceDouble / getInstance().currency.getParityToEur();
                }
            }
        }

        return priceDouble;
    }

    /**
     * Metoda ce seteaza currency-ul de pe store
     *
     * @param name Numele currency-ului in care se doreste sa fie preturile de pe store
     */
    public void setStoreCurrency(String name) {
        Currency setCurrency = new Currency(name);
        try {
            getInstance().changeCurrency(setCurrency);
        } catch (CurrencyNotFoundException exception) {
            exception.printMessage();
        }
    }


    /**
     * Metoda ce updateaza paritatea unui currency fata de EUR
     *
     * @param name        Numele currency-ului primit
     * @param parityToEUR Noua paritate fata de EUR
     */
    public void updateParity(String name, double parityToEUR) {
        for (Currency x : getInstance().currencyList) {
            if (x.getName().equals(name)) {
                x.updateParity(parityToEUR);
                return;
            }
        }
    }

    /**
     * Metoda ce afiseaza detaliile unui produs daca acesta exista in store
     *
     * @param id Id-ul unic al produsului
     */
    public void showProduct(String id) {
        for (Product x : getInstance().products) {
            if (x.getUniqueId().equals(id)) {
                System.out.println(x.toString());
                return;
            }
        }

        System.out.println("Produsul nu exista in magazin");
    }

    /**
     * Metoda ce afiseaza detaliile tuturor producatorilor de pe store
     */
    public void listManufacturers() {
        if (manufacturers == null) return;
        for (Manufacturer x : getInstance().manufacturers) {
            System.out.println(x.toString());
        }
    }

    /**
     * Metoda ce afiseaza toate produsele unui producator
     *
     * @param name Numele producatorului
     */
    public void listProductsByManufacturarer(String name) {
        for (Product x : getInstance().products) {
            if (x.getManufacturer().getName().equals(name)) {
                System.out.println(x.toString());
            }
        }
    }

    /**
     * Metoda ce afiseaza toate discounturile disponibile pe store
     */
    public void listDiscounts() {
        if (getInstance().discounts == null) {
            return;
        }
        for (Discount x : getInstance().discounts) {
            System.out.println(x.toString());
        }
    }

    /**
     * Metoda ce aplica un discount primit la toate produsele de pe store
     *
     * @param discount Discountul aplicat
     * @throws NegativePriceException    Exceptie pentru cazul in care un pret ar devenii negativ
     *                                   dupa aplicare discountului
     * @throws DiscountNotFoundException Exceptie pentru cazul in care discountul nu se gaseste in
     *                                   lista de pe store
     */
    public void applyDiscount(Discount discount) throws NegativePriceException, DiscountNotFoundException {
        for (Discount x : discounts) {
            if (x.getDiscountType().equals(discount.getDiscountType()) && x.getValue() == discount.getValue()) {
                x.setAsAppliedNow();

                /* Verificam tipul discountului aplicat*/
                if (discount.getDiscountType() == DiscountType.PERCENTAGE_DISCOUNT) {

                    /* Aplicam discountul pentru toate produsele de pe store*/
                    for (Product y : getInstance().products) {

                        /* Verificam daca pretul ar devenii negativ dupa aplicarea discountului*/
                        if (100.0 - x.getValue() > 0) {
                            y.setPrice(y.getPrice() * (100.0 - x.getValue()) / 100.0);
                        } else {
                            throw new NegativePriceException();
                        }
                    }
                } else if (discount.getDiscountType() == DiscountType.FIXED_DISCOUNT) {

                    /* Aplicam discountul pentru toate produsele de pe store*/
                    for (Product y : getInstance().products) {

                        /* Verificam daca pretul ar devenii negativ dupa aplicarea discountului*/
                        if (y.getPrice() - x.getValue() > 0) {
                            y.setPrice(y.getPrice() - x.getValue());
                        } else {
                            throw new NegativePriceException();
                        }
                    }
                }

                return;
            }
        }

        /* In cazul in care nu s-a gasit discountul pe lista din store*/
        throw new DiscountNotFoundException();
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

    public void loadStore(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        instance = (Store) in.readObject();
    }

    public void saveStore(String fileName) throws IOException {
        ObjectOutputStream outputStream = null;
        outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
        outputStream.writeObject(instance);
    }
}
