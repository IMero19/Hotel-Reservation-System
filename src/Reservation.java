import java.util.ArrayList;

public class Reservation implements RoomManagement {
    private int reservationId;
    private Guest guest;
    private String checkInDate;
    private String checkOutDate;
    private ArrayList<Room> rooms;
    private int roomCount;
    private int numberOfDays;
    private double totalCost;
    private String status;
    private Payment payment;
    private String specialRemarks;

    public Reservation(int reservationId, Guest guest, int numberOfDays,
                       String checkInDate, String checkOutDate, int roomCount,
                       String paymentMethod, String specialRemarks) {
        this.reservationId = reservationId;
        this.guest = guest;
        this.numberOfDays = numberOfDays;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomCount = roomCount;
        this.specialRemarks = specialRemarks != null ? specialRemarks : "";
        this.rooms = new ArrayList<>();
        this.payment = new Payment(reservationId, 0, paymentMethod != null ? paymentMethod : "Card");
        this.totalCost = 0;
        this.status = "Pending";
    }

    // Backward-compatible constructor
    public Reservation(int reservationId, Guest guest, int numberOfDays,
                       String checkInDate, String checkOutDate, int roomCount) {
        this(reservationId, guest, numberOfDays, checkInDate, checkOutDate, roomCount, "Card", "");
    }

    public int getReservationId() { return reservationId; }
    public Guest getGuest() { return guest; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public int getNumberOfDays() { return numberOfDays; }
    public int getRoomCount() { return roomCount; }
    public String getStatus() { return status; }
    public Payment getPayment() { return payment; }
    public ArrayList<Room> getReservationRooms() { return rooms; }
    public String getSpecialRemarks() { return specialRemarks; }

    public void setCheckInDate(String date) { this.checkInDate = date; }
    public void setCheckOutDate(String date) { this.checkOutDate = date; }
    public void setNumberOfDays(int days) { this.numberOfDays = days; calculateReservationTotalCost(); }
    public void setStatus(String status) { this.status = status; }
    public void setSpecialRemarks(String remarks) { this.specialRemarks = remarks; }

    public double getTotalCost() { return calculateReservationTotalCost(); }

    public double calculateReservationTotalCost() {
        totalCost = 0;
        for (Room room : rooms) totalCost += room.getPricePerNight();
        totalCost *= numberOfDays;
        payment.setAmount(totalCost);
        return totalCost;
    }

    @Override
    public void addRoom(Room room) {
        if (room == null) { System.out.println("Invalid room."); return; }
        if (rooms.contains(room)) { System.out.println("Room already in reservation."); return; }
        if (rooms.size() < roomCount) {
            rooms.add(room);
            calculateReservationTotalCost();
            System.out.println("Room " + room.getRoomCode() + " added to reservation.");
        } else {
            System.out.println("Room count limit (" + roomCount + ") reached.");
        }
    }

    @Override
    public void removeRoom(Room room) {
        if (rooms.remove(room)) {
            calculateReservationTotalCost();
            System.out.println("Room removed from reservation.");
        } else {
            System.out.println("Room not found in reservation.");
        }
    }

    public void confirmReservation() {
        if (status.equals("Cancelled")) { System.out.println("Cancelled reservation cannot be confirmed."); return; }
        if (status.equals("Confirmed")) { System.out.println("Already confirmed."); return; }
        if (rooms.isEmpty()) { System.out.println("No rooms in reservation."); return; }

        for (Room room : rooms) {
            if (!room.isAvailable()) {
                System.out.println("Room " + room.getRoomCode() + " is not available.");
                return;
            }
        }
        for (Room room : rooms) {
            room.setAvailable(false);
            System.out.println("Room " + room.getRoomCode() + " reserved for " + numberOfDays + " days.");
        }
        calculateReservationTotalCost();
        payment.processPayment();
        status = "Confirmed";
    }

    public void cancelReservation() {
        if (status.equals("Cancelled")) { System.out.println("Already cancelled."); return; }
        for (Room room : rooms) {
            room.setAvailable(true);
            System.out.println("Room " + room.getRoomCode() + " is now available.");
        }
        status = "Cancelled";
        payment.refundPayment();
    }

    public void displayReservationDetails() {
        calculateReservationTotalCost();
        System.out.printf("Reservation ID: %d | Guest: %s | Days: %d | Check-In: %s | Check-Out: %s%n",
                reservationId, guest.getFullName(), numberOfDays, checkInDate, checkOutDate);
        System.out.println("Status: " + status + " | Remarks: " + specialRemarks);
        System.out.println("Rooms:");
        for (Room room : rooms) room.displayRoomInfo();
        System.out.printf("Total Cost: $%.2f | Payment: %s%n", totalCost, payment.getPaymentStatus());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Reservation)) return false;
        Reservation r = (Reservation) obj;
        return this.reservationId == r.reservationId;
    }

    @Override
    public int hashCode() { return Integer.hashCode(reservationId); }
}
