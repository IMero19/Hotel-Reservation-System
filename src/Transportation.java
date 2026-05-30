public class Transportation {
    private int transID;
    private String name;
    private String type;
    private double price;
    private double rating;

    public Transportation(int transID, String name, String type, double price, double rating) {
        this.transID = transID;
        this.name = name;
        this.type = type;
        this.price = price;
        this.rating = rating;
    }

    public int getTransID() { return transID; }
    public String getTransportationName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }

    public void displayTransportationInfo() {
        System.out.printf("Transportation [%d] %s (%s) | $%.2f | Rating: %.1f%n", transID, name, type, price, rating);
    }

    @Override
    public String toString() { return name + " (" + type + ") - $" + String.format("%.2f", price); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transportation)) return false;
        Transportation t = (Transportation) obj;
        return this.transID == t.transID;
    }

    @Override
    public int hashCode() { return Integer.hashCode(transID); }
}
