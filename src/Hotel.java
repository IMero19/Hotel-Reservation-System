import java.util.ArrayList;

public class Hotel implements ReservationManagement, RoomManagement {
    private String hotelName;
    private String address;
    private double rating;
    private ArrayList<Room> rooms;
    private ArrayList<Reservation> reservations;
    private ArrayList<Building> buildings;
    private ArrayList<Service> services;
    private ArrayList<RoomService> roomServices;
    private ArrayList<Payment> payments;
    private ArrayList<Employee> employees;
    private ArrayList<Manager> managers;
    private ArrayList<Guest> guests;
    private ArrayList<Department> departments;
    private ArrayList<Transportation> transportations;
    private ArrayList<Parking> parkingAreas;
    private ArrayList<Restaurant> restaurants;

    public Hotel(String hotelName, String address, double rating) {
        this.hotelName = hotelName;
        this.address = address;
        this.rating = rating;
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.services = new ArrayList<>();
        this.roomServices = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.managers = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.departments = new ArrayList<>();
        this.transportations = new ArrayList<>();
        this.parkingAreas = new ArrayList<>();
        this.restaurants = new ArrayList<>();

        // Default services available in the hotel
        services.add(new Service(1, "Spa", 70.0));
        services.add(new Service(2, "Laundry", 25.0));
        services.add(new Service(3, "Breakfast", 18.0));
        services.add(new Service(4, "Room Cleaning", 15.0));
        services.add(new Service(5, "Airport Transfer", 45.0));
    }

    // --- Getters ---
    public String getHotelName() { return hotelName; }
    public String getHotelAddress() { return address; }
    public double getHotelRating() { return rating; }
    public ArrayList<Room> getRooms() { return rooms; }
    public ArrayList<Reservation> getReservations() { return reservations; }
    public ArrayList<Building> getBuildings() { return buildings; }
    public ArrayList<Service> getServices() { return services; }
    public ArrayList<Employee> getEmployees() { return employees; }
    public ArrayList<Manager> getManagers() { return managers; }
    public ArrayList<Guest> getGuests() { return guests; }
    public ArrayList<Department> getDepartments() { return departments; }
    public ArrayList<Transportation> getTransportations() { return transportations; }
    public ArrayList<Parking> getParkingAreas() { return parkingAreas; }
    public ArrayList<Restaurant> getRestaurants() { return restaurants; }
    public ArrayList<RoomService> getRoomServices() { return roomServices; }
    public ArrayList<Payment> getPayments() { return payments; }

    // --- Room Management ---
    @Override
    public void addRoom(Room room) {
        if (room == null) return;
        if (rooms.contains(room)) { System.out.println("Room " + room.getRoomCode() + " already exists."); return; }
        rooms.add(room);
        System.out.println("Room " + room.getRoomCode() + " added.");
    }

    @Override
    public void removeRoom(Room room) {
        if (room == null) return;
        if (rooms.remove(room)) System.out.println("Room " + room.getRoomCode() + " removed.");
        else System.out.println("Room not found.");
    }

    public Room searchRoom(int roomCode) {
        for (Room r : rooms) { if (r.getRoomCode() == roomCode) return r; }
        return null;
    }

    public void showRooms() {
        if (rooms.isEmpty()) { System.out.println("No rooms in hotel."); return; }
        System.out.println("All Rooms:");
        for (Room r : rooms) r.displayRoomInfo();
    }

    public void showAvailableRooms() {
        System.out.println("Available Rooms:");
        boolean found = false;
        for (Room r : rooms) { if (r.isAvailable()) { r.displayRoomInfo(); found = true; } }
        if (!found) System.out.println("No available rooms.");
    }

    public void resetRoomAvailability(int roomCode) {
        Room room = searchRoom(roomCode);
        if (room != null) { room.setAvailable(true); System.out.println("Room " + roomCode + " availability reset."); }
        else System.out.println("Room not found.");
    }

    // --- Reservation Management ---
    @Override
    public void addReservation(Reservation reservation) {
        if (reservation == null || reservation.getGuest() == null) return;
        if (reservations.contains(reservation)) { System.out.println("Reservation already exists."); return; }
        reservations.add(reservation);
        System.out.println("Reservation " + reservation.getReservationId() + " added.");
    }

    @Override
    public void cancelReservation(Reservation reservation) {
        if (reservation == null) return;
        if (reservations.contains(reservation)) {
            reservation.cancelReservation();
            System.out.println("Reservation " + reservation.getReservationId() + " cancelled.");
        } else {
            System.out.println("Reservation not found.");
        }
    }

    public void deleteReservation(Reservation reservation) {
        if (reservation == null) return;
        if (reservations.remove(reservation)) {
            reservation.cancelReservation();
            System.out.println("Reservation " + reservation.getReservationId() + " deleted.");
        } else {
            System.out.println("Reservation not found.");
        }
    }

    public Reservation searchReservation(int reservationId) {
        for (Reservation r : reservations) { if (r.getReservationId() == reservationId) return r; }
        return null;
    }

    public void showReservations() {
        if (reservations.isEmpty()) { System.out.println("No reservations."); return; }
        for (Reservation r : reservations) { r.displayReservationDetails(); System.out.println(); }
    }

    public void showGuestReservations(Guest guest) {
        if (guest == null) return;
        boolean found = false;
        for (Reservation r : reservations) {
            if (r.getGuest().equals(guest)) { r.displayReservationDetails(); found = true; }
        }
        if (!found) System.out.println("No reservations for " + guest.getFullName());
    }

    // --- Guest Management ---
    public void addGuest(Guest guest) {
        if (guest == null) return;
        if (guests.contains(guest)) { System.out.println("Guest " + guest.getId() + " already exists."); return; }
        guests.add(guest);
        System.out.println("Guest " + guest.getFullName() + " added.");
    }

    public void removeGuest(Guest guest) {
        if (guest == null) return;
        if (guests.remove(guest)) System.out.println("Guest " + guest.getFullName() + " removed.");
        else System.out.println("Guest not found.");
    }

    public Guest searchGuest(int guestId) {
        for (Guest g : guests) { if (g.getId() == guestId) return g; }
        return null;
    }

    public void updateGuest(int guestId, String phone, String email) {
        Guest g = searchGuest(guestId);
        if (g != null) { g.setPhoneNumber(phone); g.setEmail(email); System.out.println("Guest updated."); }
        else System.out.println("Guest not found.");
    }

    public void showGuests() {
        if (guests.isEmpty()) { System.out.println("No guests."); return; }
        for (Guest g : guests) System.out.println(g.getDetails());
    }

    // --- Employee Management ---
    public void addEmployee(Employee employee) {
        if (employee == null) return;
        if (employees.contains(employee)) { System.out.println("Employee already exists."); return; }
        employees.add(employee);
        System.out.println("Employee " + employee.getFullName() + " added.");
    }

    public void removeEmployee(Employee employee) {
        if (employee == null) return;
        if (employees.remove(employee)) System.out.println("Employee " + employee.getFullName() + " removed.");
        else System.out.println("Employee not found.");
    }

    // Fixed: actually updates the employee's fields
    public void updateEmployee(int employeeId, String phone, String position, double salary) {
        Employee emp = searchEmployee(employeeId);
        if (emp != null) {
            emp.setPhoneNumber(phone);
            emp.updatePosition(position);
            emp.updateSalary(salary);
            System.out.println("Employee " + emp.getFullName() + " updated.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public Employee searchEmployee(int employeeId) {
        for (Employee e : employees) { if (e.getId() == employeeId) return e; }
        return null;
    }

    public void showEmployees() {
        if (employees.isEmpty()) { System.out.println("No employees."); return; }
        for (Employee e : employees) System.out.println(e.getDetails());
    }

    // --- Manager Management ---
    public void addManager(Manager manager) {
        if (manager == null) return;
        if (!managers.contains(manager)) managers.add(manager);
        if (!employees.contains(manager)) employees.add(manager);
        System.out.println("Manager " + manager.getFullName() + " added.");
    }

    public void removeManager(Manager manager) {
        if (manager == null) return;
        if (managers.remove(manager)) { employees.remove(manager); System.out.println("Manager removed."); }
        else System.out.println("Manager not found.");
    }

    public void showManagers() {
        if (managers.isEmpty()) { System.out.println("No managers."); return; }
        for (Manager m : managers) System.out.println(m.getDetails());
    }

    // --- Department Management ---
    public void addDepartment(Department department) {
        if (department == null) return;
        if (!departments.contains(department)) { departments.add(department); System.out.println("Department added."); }
        else System.out.println("Department already exists.");
    }

    public void removeDepartment(Department department) {
        if (department == null) return;
        if (departments.remove(department)) System.out.println("Department removed.");
        else System.out.println("Department not found.");
    }

    public Department searchDepartment(int departmentId) {
        for (Department d : departments) { if (d.getDepartmentId() == departmentId) return d; }
        return null;
    }

    public void showDepartments() {
        if (departments.isEmpty()) { System.out.println("No departments."); return; }
        for (Department d : departments) { d.displayDepartmentDetails(); System.out.println(); }
    }

    // --- Building Management ---
    public void addBuilding(Building building) {
        if (building == null || buildings.contains(building)) return;
        buildings.add(building);
        System.out.println("Building added.");
    }

    public void removeBuilding(Building building) {
        if (buildings.remove(building)) System.out.println("Building removed.");
    }

    public void showBuildings() {
        for (Building b : buildings) System.out.println(b);
    }

    // --- Service Management ---
    public void addService(Service service) {
        if (service == null || services.contains(service)) return;
        services.add(service);
        System.out.println("Service added.");
    }

    public void showServices() {
        for (Service s : services) s.displayServiceInfo();
    }

    public void addRoomService(RoomService roomService) {
        if (roomService == null || roomServices.contains(roomService)) return;
        roomServices.add(roomService);
    }

    public RoomService getRoomServiceForRoom(Room room) {
        for (RoomService rs : roomServices) { if (rs.getRoom().equals(room)) return rs; }
        return null;
    }

    public void showRoomServices() {
        if (roomServices.isEmpty()) { System.out.println("No room service records."); return; }
        for (RoomService rs : roomServices) rs.displayOrders();
    }

    // --- Payment Management ---
    public void addPayment(Payment payment) {
        if (payment == null || payments.contains(payment)) return;
        payments.add(payment);
    }

    public void showPayments() {
        for (Payment p : payments) p.displayPaymentInfo();
    }

    // --- Transportation ---
    public void addTransportation(Transportation t) {
        if (t == null || transportations.contains(t)) return;
        transportations.add(t);
    }

    public void showTransportations() {
        for (Transportation t : transportations) t.displayTransportationInfo();
    }

    // --- Parking ---
    public void addParking(Parking p) {
        if (p == null || parkingAreas.contains(p)) return;
        parkingAreas.add(p);
    }

    public void showParkingAreas() {
        for (Parking p : parkingAreas) p.displayParkingInfo();
    }

    // --- Restaurant ---
    public void addRestaurant(Restaurant r) {
        if (r == null || restaurants.contains(r)) return;
        restaurants.add(r);
    }

    public void showRestaurants() {
        for (Restaurant r : restaurants) r.displayRestaurantInfo();
    }

    public void displayHotelDetails() {
        System.out.println("=== " + hotelName + " ===");
        System.out.println("Address: " + address + " | Rating: " + rating);
        System.out.println("Rooms: " + rooms.size() + " | Reservations: " + reservations.size());
        System.out.println("Guests: " + guests.size() + " | Employees: " + employees.size());
        System.out.println("Departments: " + departments.size() + " | Services: " + services.size());
    }
}
