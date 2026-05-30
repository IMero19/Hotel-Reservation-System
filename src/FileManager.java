import java.io.*;
import java.util.*;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String GUESTS_FILE = DATA_DIR + "guests.txt";
    private static final String EMPLOYEES_FILE = DATA_DIR + "employees.txt";
    private static final String ROOMS_FILE = DATA_DIR + "rooms.txt";
    private static final String RESERVATIONS_FILE = DATA_DIR + "reservations.txt";

    public static void loadAll() {
        new File(DATA_DIR).mkdirs();
        loadUsers();
        loadRooms();
        loadGuests();
        loadEmployees();
        loadReservations();

        if (HotelApp.getUserAccounts().isEmpty()) {
            seedDefaultData();
        }
    }

    public static void saveAll() {
        new File(DATA_DIR).mkdirs();
        saveUsers();
        saveGuests();
        saveEmployees();
        saveRooms();
        saveReservations();
    }

    // ---- USERS ----
    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    UserAccount ua = UserAccount.fromFileString(line);
                    HotelApp.getUserAccounts().add(ua);
                    HotelApp.setNextUserId(ua.getAccountId());
                } catch (InvalidInputException e) {
                    System.err.println("Skipping bad user record: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users: " + e.getMessage());
        }
    }

    private static void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (UserAccount ua : HotelApp.getUserAccounts()) {
                writer.println(ua.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // ---- GUESTS ----
    // Format: id|firstName|lastName|email|age|gender|phone
    private static void loadGuests() {
        File file = new File(GUESTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split("\\|");
                    if (p.length < 7) continue;
                    int id = Integer.parseInt(p[0].trim());
                    String firstName = p[1].trim();
                    String lastName = p[2].trim();
                    String email = p[3].trim();
                    int age = Integer.parseInt(p[4].trim());
                    char gender = p[5].trim().charAt(0);
                    String phone = p[6].trim();
                    Guest g = new Guest(firstName, lastName, email, id, age, gender, phone);
                    HotelApp.getHotel().addGuest(g);
                    HotelApp.setNextGuestId(id);
                } catch (Exception e) {
                    System.err.println("Skipping bad guest record: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading guests: " + e.getMessage());
        }
    }

    private static void saveGuests() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GUESTS_FILE))) {
            for (Guest g : HotelApp.getHotel().getGuests()) {
                writer.println(g.getId() + "|" + g.getFirstName() + "|" + g.getLastName() + "|"
                        + g.getEmail() + "|" + g.getAge() + "|" + g.getGender() + "|" + g.getPhoneNumber());
            }
        } catch (IOException e) {
            System.err.println("Error saving guests: " + e.getMessage());
        }
    }

    // ---- EMPLOYEES ----
    // Format: id|firstName|lastName|phone|email|code|salary|position|gender|age
    private static void loadEmployees() {
        File file = new File(EMPLOYEES_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split("\\|");
                    if (p.length < 10) continue;
                    int id = Integer.parseInt(p[0].trim());
                    String firstName = p[1].trim();
                    String lastName = p[2].trim();
                    String phone = p[3].trim();
                    String email = p[4].trim();
                    String code = p[5].trim();
                    double salary = Double.parseDouble(p[6].trim());
                    String position = p[7].trim();
                    char gender = p[8].trim().charAt(0);
                    int age = Integer.parseInt(p[9].trim());

                    Employee emp;
                    if ("Manager".equalsIgnoreCase(position)) {
                        emp = new Manager(id, firstName, lastName, phone, email, code, salary, age, gender);
                        HotelApp.getHotel().addManager((Manager) emp);
                    } else {
                        emp = new Employee(id, firstName, lastName, phone, email, code, salary, position, gender, age);
                        HotelApp.getHotel().addEmployee(emp);
                    }
                    HotelApp.setNextEmployeeId(id);
                } catch (Exception e) {
                    System.err.println("Skipping bad employee record: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employees: " + e.getMessage());
        }
    }

    private static void saveEmployees() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            for (Employee emp : HotelApp.getHotel().getEmployees()) {
                writer.println(emp.getId() + "|" + emp.getFirstName() + "|" + emp.getLastName() + "|"
                        + emp.getPhoneNumber() + "|" + emp.getEmail() + "|" + emp.getEmployeeCode() + "|"
                        + emp.getSalary() + "|" + emp.getPosition() + "|" + emp.getGender() + "|" + emp.getAge());
            }
        } catch (IOException e) {
            System.err.println("Error saving employees: " + e.getMessage());
        }
    }

    // ---- ROOMS ----
    // Format: roomCode|type|pricePerNight|view|floorNo|isAvailable
    private static void loadRooms() {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split("\\|");
                    if (p.length < 6) continue;
                    int code = Integer.parseInt(p[0].trim());
                    String type = p[1].trim();
                    double price = Double.parseDouble(p[2].trim());
                    String view = p[3].trim();
                    int floor = Integer.parseInt(p[4].trim());
                    boolean available = Boolean.parseBoolean(p[5].trim());
                    Room r = new Room(code, type, price, view, floor);
                    r.setAvailable(available);
                    HotelApp.getHotel().addRoom(r);
                } catch (Exception e) {
                    System.err.println("Skipping bad room record: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading rooms: " + e.getMessage());
        }
    }

    private static void saveRooms() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : HotelApp.getHotel().getRooms()) {
                writer.println(r.getRoomCode() + "|" + r.getRoomType() + "|" + r.getPricePerNight()
                        + "|" + r.getRoomView() + "|" + r.getFloorNo() + "|" + r.isAvailable());
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    // ---- RESERVATIONS ----
    // Format: resId|guestId|days|checkIn|checkOut|status|payMethod|payStatus|roomCodes|remarks
    private static void loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split("\\|", 10);
                    if (p.length < 9) continue;
                    int resId = Integer.parseInt(p[0].trim());
                    int guestId = Integer.parseInt(p[1].trim());
                    int days = Integer.parseInt(p[2].trim());
                    String checkIn = p[3].trim();
                    String checkOut = p[4].trim();
                    String status = p[5].trim();
                    String payMethod = p[6].trim();
                    String payStatus = p[7].trim();
                    String roomCodes = p[8].trim();
                    String remarks = p.length > 9 ? p[9].trim() : "";

                    Guest guest = HotelApp.getHotel().searchGuest(guestId);
                    if (guest == null) continue;

                    List<Room> resRooms = new ArrayList<>();
                    if (!roomCodes.isEmpty()) {
                        for (String code : roomCodes.split(",")) {
                            Room r = HotelApp.getHotel().searchRoom(Integer.parseInt(code.trim()));
                            if (r != null) resRooms.add(r);
                        }
                    }

                    Reservation res = new Reservation(resId, guest, days, checkIn, checkOut,
                            resRooms.size() > 0 ? resRooms.size() : 1, payMethod, remarks);

                    for (Room r : resRooms) {
                        res.addRoom(r);
                        if ("Confirmed".equals(status)) r.setAvailable(false);
                    }

                    res.setStatus(status);
                    res.getPayment().setPaymentMethod(payMethod);
                    res.getPayment().setPaymentStatus(payStatus);
                    res.getPayment().setAmount(res.calculateReservationTotalCost());

                    HotelApp.getHotel().getReservations().add(res);
                    HotelApp.getHotel().getPayments().add(res.getPayment());
                    HotelApp.setNextReservationId(resId);

                } catch (Exception e) {
                    System.err.println("Skipping bad reservation record: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading reservations: " + e.getMessage());
        }
    }

    private static void saveReservations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            for (Reservation res : HotelApp.getHotel().getReservations()) {
                StringBuilder roomCodes = new StringBuilder();
                for (Room r : res.getReservationRooms()) {
                    if (roomCodes.length() > 0) roomCodes.append(",");
                    roomCodes.append(r.getRoomCode());
                }
                writer.println(res.getReservationId() + "|" + res.getGuest().getId() + "|"
                        + res.getNumberOfDays() + "|" + res.getCheckInDate() + "|" + res.getCheckOutDate() + "|"
                        + res.getStatus() + "|" + res.getPayment().getPaymentMethod() + "|"
                        + res.getPayment().getPaymentStatus() + "|" + roomCodes + "|" + res.getSpecialRemarks());
            }
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    // ---- SEED DEFAULT DATA (first run) ----
    private static void seedDefaultData() {
        // Default admin account
        UserAccount admin = new UserAccount(1, "admin", "admin123", "ADMIN", 0);
        HotelApp.getUserAccounts().add(admin);
        HotelApp.setNextUserId(1);

        // Default manager
        Manager mgr = new Manager(10, "Sophia", "Clark", "555-3030", "sophia@hotel.com",
                "MGR-10", 8500.0, 35, 'F');
        HotelApp.getHotel().addManager(mgr);
        UserAccount mgrAccount = new UserAccount(2, "manager1", "mgr123", "MANAGER", 10);
        HotelApp.getUserAccounts().add(mgrAccount);
        HotelApp.setNextEmployeeId(10);
        HotelApp.setNextUserId(2);

        // Default receptionist
        Employee rec = new Employee(11, "David", "Kim", "555-4040", "david@hotel.com",
                "REC-11", 3800.0, "Receptionist", 'M', 28);
        HotelApp.getHotel().addEmployee(rec);
        UserAccount recAccount = new UserAccount(3, "reception1", "rec123", "RECEPTIONIST", 11);
        HotelApp.getUserAccounts().add(recAccount);
        HotelApp.setNextEmployeeId(11);
        HotelApp.setNextUserId(3);

        // Default rooms
        Hotel h = HotelApp.getHotel();
        h.addRoom(new Room(101, "Single", 80.0, "City", 1));
        h.addRoom(new Room(102, "Double", 120.0, "Garden", 1));
        h.addRoom(new Room(201, "Suite", 250.0, "Ocean", 2));
        h.addRoom(new Room(202, "Double", 130.0, "Pool", 2));
        h.addRoom(new Room(203, "Single", 80.0, "City", 2));
        h.addRoom(new Room(301, "Suite", 300.0, "Ocean", 3));

        // Default guests
        h.addGuest(new Guest("John", "Doe", "john@email.com", 1, 35, 'M', "555-1234"));
        h.addGuest(new Guest("Jane", "Smith", "jane@email.com", 2, 28, 'F', "555-5678"));
        HotelApp.setNextGuestId(2);

        saveAll();
        System.out.println("Default data seeded and saved.");
    }
}
