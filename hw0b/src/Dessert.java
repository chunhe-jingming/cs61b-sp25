public class Dessert {
    /** Flavor of the dessert. */
    private int flavor;
    /** Price of the dessert. */
    private int price;
    /** Number of dessert objects created so far. */
    private static int numDesserts = 0;

    /** Creates a dessert with the given flavor and price, and keeps a
     * count of how many desserts have been created.
     * @param flavor Flavor of the dessert
     * @param price Price of the dessert
     */
    public Dessert(int flavor, int price) {
        this.flavor = flavor;
        this.price = price;
        numDesserts++;
    }

    /** Prints the flavor, price, and number of desserts created so far. */
    public void printDessert() {
        System.out.println(flavor + " " + price + " " + numDesserts);
    }

    public static void main(String[] args) {
        System.out.println("I love dessert!");
    }
}
