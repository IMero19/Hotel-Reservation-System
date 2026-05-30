public class Room {
    private int roomCode;
    private String type;
    private double pricePerNight;
    private String view;
    private int floorNo;
    private boolean isAvailable;

    public Room(int roomCode, String type, double pricePerNight, String view, int floorNo) {
        this.roomCode = roomCode;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.view = view;
        this.floorNo = floorNo;
        this.isAvailable = true;
    }

    public int getRoomCode() { return roomCode; }
    public String getRoomType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public String getRoomView() { return view; }
    public int getFloorNo() { return floorNo; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean status) { this.isAvailable = status; }
    public void setType(String type) { this.type = type; }
    public void setPricePerNight(double price) { this.pricePerNight = price; }
    public void setView(String view) { this.view = view; }

    public void displayRoomInfo() {
        System.out.printf("Room %d | %s | $%.2f/night | Floor %d | View: %s | %s%n",
                roomCode, type, pricePerNight, floorNo, view, isAvailable ? "Available" : "Booked");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Room)) return false;
        Room r = (Room) obj;
        return this.roomCode == r.roomCode;
    }

    @Override
    public int hashCode() { return Integer.hashCode(roomCode); }

    @Override
    public String toString() {
        return "Room " + roomCode + " (" + type + ") - $" + String.format("%.2f", pricePerNight) + "/night | " + (isAvailable ? "Available" : "Booked");
    }
}
