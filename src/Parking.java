public class Parking {
    private int parkingAreaNo;
    private boolean availability;
    private String locationLetter;

    public Parking(int parkingAreaNo, boolean availability, String locationLetter) {
        this.parkingAreaNo = parkingAreaNo;
        this.availability = availability;
        this.locationLetter = locationLetter;
    }

    public int getParkingAreaNo() { return parkingAreaNo; }
    public boolean isAvailable() { return availability; }
    public String getLocationLetter() { return locationLetter; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    public void displayParkingInfo() {
        System.out.println("Parking Area " + parkingAreaNo + " | Zone " + locationLetter + " | " + (availability ? "Available" : "Occupied"));
    }

    @Override
    public String toString() { return "Parking " + parkingAreaNo + " (Zone " + locationLetter + ") - " + (availability ? "Available" : "Occupied"); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Parking)) return false;
        Parking p = (Parking) obj;
        return this.parkingAreaNo == p.parkingAreaNo;
    }

    @Override
    public int hashCode() { return Integer.hashCode(parkingAreaNo); }
}
