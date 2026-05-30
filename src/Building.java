import java.util.ArrayList;

public class Building {
    private int buildingNo;
    private String buildingLocation;
    private int numberOfFloors;
    private ArrayList<Room> rooms;

    public Building(int buildingNo, String buildingLocation, int numberOfFloors) {
        this.buildingNo = buildingNo;
        this.buildingLocation = buildingLocation;
        this.numberOfFloors = numberOfFloors;
        this.rooms = new ArrayList<>();
    }

    public int getBuildingNo() { return buildingNo; }
    public String getBuildingLocation() { return buildingLocation; }
    public int getNumberOfFloors() { return numberOfFloors; }
    public ArrayList<Room> getRooms() { return rooms; }

    public void addRoom(Room room) {
        if (room != null && !rooms.contains(room)) {
            rooms.add(room);
            System.out.println("Room " + room.getRoomCode() + " added to " + buildingLocation + " building.");
        }
    }

    public void displayBuildingInfo() {
        System.out.println("Building " + buildingNo + " | " + buildingLocation + " | Floors: " + numberOfFloors + " | Rooms: " + rooms.size());
    }

    @Override
    public String toString() { return "Building " + buildingNo + " - " + buildingLocation + " (" + numberOfFloors + " floors, " + rooms.size() + " rooms)"; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Building)) return false;
        Building b = (Building) obj;
        return this.buildingNo == b.buildingNo;
    }

    @Override
    public int hashCode() { return Integer.hashCode(buildingNo); }
}
