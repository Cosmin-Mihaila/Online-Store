
import java.io.*;
import java.util.Arrays;

public class Tema {
    public static void main(String[] args) throws IOException, CurrencyNotFoundException, ClassNotFoundException {
        Store st = Store.getInstance();
        st.readCSV("Book1.csv");
        st.showStore();
        if(false){
            while(true){
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String newCommand = br.readLine();
                String[] newCommandsSplit = newCommand.split(" ", 6);
                System.out.println(newCommand);
                if(newCommandsSplit[0].equals("quit") || newCommandsSplit[0].equals("exit")){
                    break;
                }
                else if (newCommandsSplit[0].equals("listcurrencies")){
                    st.listCurrencies();
                }
                else if (newCommandsSplit[0].equals("getstorecurrency")){
                    System.out.println(st.getStoreCurrency());
                }
                else if (newCommandsSplit[0].equals("addcurrency")){
                    st.createCurrency(newCommandsSplit[1], newCommandsSplit[2], Double.parseDouble(newCommandsSplit[3]));
                }
                else if(newCommandsSplit[0].equals("setstorecurrency")){
                    st.setStoreCurrency(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("listproducts")){
                    st.showStore();
                }
                else if(newCommandsSplit[0].equals("updateparity")){
                    st.updateParity(newCommandsSplit[1], Double.parseDouble(newCommandsSplit[2]));
                }
                else if(newCommandsSplit[0].equals("showproduct")){
                    st.showProduct(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("listmanufacturers")){
                    st.listManufacturers();
                }
                else if(newCommandsSplit[0].equals("listproductsbymanufacturarer")){
                    st.listProductsByManufacturarer(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("addiscount")){
                    String[] nameList = new String[newCommandsSplit.length - 3];
                    System.arraycopy(newCommandsSplit, 3, nameList, 0, newCommandsSplit.length - 3);
                    String name = String.join(" ", Arrays.asList(nameList));

                    st.createDiscount(DiscountType.valueOf(newCommandsSplit[1] + "_DISCOUNT"), name, Double.parseDouble(newCommandsSplit[2]));
                }
                else if(newCommandsSplit[0].equals("listdiscounts")){
                    st.listDiscounts();

                }
                else if(newCommandsSplit[0].equals("applydiscount")){
                    try {
                        st.applyDiscount(new Discount(DiscountType.valueOf(newCommandsSplit[1] + "_DISCOUNT"), Double.parseDouble(newCommandsSplit[2])));
                    }
                    catch (DiscountNotFoundException  exception){
                        exception.printMessage();
                    } catch (NegativePriceException exception) {
                        exception.printMessage();
                    }
                }
                else if (newCommandsSplit[0].equals("calculatetotal")){
                    String[] aux = new String[newCommandsSplit.length - 1];
                    System.arraycopy(newCommandsSplit, 1, aux, 0, newCommandsSplit.length - 1);
                    st.calculateTotal(aux);
                }
                else if(newCommandsSplit[0].equals("loadcsv")){
                    st.readCSV(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("savecsv")){
                    st.writeCSV(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("savestore")){
                    st.saveStore(newCommandsSplit[1]);
                }
                else if(newCommandsSplit[0].equals("loadstore")){
                    st.loadStore(newCommandsSplit[1]);
                }
            }
        }
    }
}
