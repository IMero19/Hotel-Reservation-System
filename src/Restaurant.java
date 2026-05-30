import java.util.ArrayList;

public class Restaurant {
    private String restaurantName;
    private String cuisineType;
    private int capacity;
    private boolean isOpen;
    private ArrayList<MenuItem> menu;

    public Restaurant(String restaurantName, String cuisineType, int capacity) {
        this.restaurantName = restaurantName;
        this.cuisineType = cuisineType;
        this.capacity = capacity;
        this.isOpen = false;
        this.menu = new ArrayList<>();
    }

    public String getRestaurantName() { return restaurantName; }
    public String getCuisineType() { return cuisineType; }
    public int getCapacity() { return capacity; }
    public boolean isOpen() { return isOpen; }
    public ArrayList<MenuItem> getMenu() { return menu; }

    public void openRestaurant() { isOpen = true; System.out.println(restaurantName + " is now open."); }
    public void closeRestaurant() { isOpen = false; System.out.println(restaurantName + " is now closed."); }

    public void addMenuItem(MenuItem item) { if (item != null) menu.add(item); }
    public void removeMenuItem(MenuItem item) { menu.remove(item); }

    public void displayMenu() {
        System.out.println("Menu - " + restaurantName);
        for (MenuItem item : menu) System.out.println("  " + item);
    }

    public void displayRestaurantInfo() {
        System.out.println(restaurantName + " | " + cuisineType + " | Capacity: " + capacity + " | " + (isOpen ? "Open" : "Closed") + " | Items: " + menu.size());
    }
}
