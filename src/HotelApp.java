import java.util.ArrayList;

public class HotelApp {
    private static final Hotel hotel = new Hotel("Grand Eagle Hotel", "123 Nile Corniche, Cairo", 4.7);
    private static final ArrayList<UserAccount> userAccounts = new ArrayList<>();

    private static int nextGuestId = 1;
    private static int nextEmployeeId = 1;
    private static int nextReservationId = 1;
    private static int nextUserId = 1;

    public static Hotel getHotel() { return hotel; }
    public static ArrayList<UserAccount> getUserAccounts() { return userAccounts; }

    public static int getNextGuestId() { return nextGuestId++; }
    public static int getNextEmployeeId() { return nextEmployeeId++; }
    public static int getNextReservationId() { return nextReservationId++; }
    public static int getNextUserId() { return nextUserId++; }

    public static void setNextGuestId(int id) { if (id >= nextGuestId) nextGuestId = id + 1; }
    public static void setNextEmployeeId(int id) { if (id >= nextEmployeeId) nextEmployeeId = id + 1; }
    public static void setNextReservationId(int id) { if (id >= nextReservationId) nextReservationId = id + 1; }
    public static void setNextUserId(int id) { if (id >= nextUserId) nextUserId = id + 1; }

    public static UserAccount authenticate(String username, String password) throws InvalidLoginException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidLoginException("Username and password cannot be empty.");
        }
        for (UserAccount ua : userAccounts) {
            if (ua.getUsername().equals(username) && ua.getPassword().equals(password)) {
                return ua;
            }
        }
        throw new InvalidLoginException("Incorrect username or password.");
    }

    public static void initialize() {
        FileManager.loadAll();
    }

    public static void save() {
        FileManager.saveAll();
    }
}
