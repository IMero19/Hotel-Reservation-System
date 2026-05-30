public class Receptionist extends Employee {
    private Hotel hotel;

    public Receptionist(int id, String firstName, String lastName, String phoneNumber, String email,
                        String employeeCode, double salary, char gender, int age, Hotel hotel) {
        super(id, firstName, lastName, phoneNumber, email, employeeCode, salary, "Receptionist", gender, age);
        this.hotel = hotel;
    }

    public Reservation createReservation(int reservationId, Guest guest, int numberOfDays,
                                          String checkInDate, String checkOutDate, int roomCount,
                                          String paymentMethod, String specialRemarks) {
        if (guest == null || !hotel.getGuests().contains(guest)) {
            System.out.println("Guest is not registered in hotel records.");
            return null;
        }
        Reservation reservation = new Reservation(reservationId, guest, numberOfDays, checkInDate, checkOutDate, roomCount, paymentMethod, specialRemarks);
        System.out.println("Reservation created successfully.");
        return reservation;
    }

    public void addRoomToReservation(Reservation reservation, Room room) {
        if (reservation != null && room != null) reservation.addRoom(room);
    }

    public void removeRoomFromReservation(Reservation reservation, Room room) {
        if (reservation != null && room != null) reservation.removeRoom(room);
    }

    public void confirmAndRegisterReservation(Reservation reservation) {
        if (reservation == null) return;
        reservation.confirmReservation();
        if (reservation.getStatus().equals("Confirmed")) {
            hotel.addReservation(reservation);
            hotel.addPayment(reservation.getPayment());
        }
    }

    public void cancelReservation(Reservation reservation) {
        if (reservation != null) hotel.cancelReservation(reservation);
    }

    @Override
    public String getDetails() {
        return "Receptionist Code: " + getEmployeeCode()
                + " | Name: " + getFirstName() + " " + getLastName()
                + " | Phone: " + getPhoneNumber();
    }
}
