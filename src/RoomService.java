import java.util.ArrayList;

public class RoomService {
    private Room room;
    private ArrayList<Service> serviceOrders;

    public RoomService(Room room) {
        this.room = room;
        this.serviceOrders = new ArrayList<>();
    }

    public Room getRoom() { return room; }
    public ArrayList<Service> getServiceOrders() { return serviceOrders; }

    public void orderService(Service service) {
        if (service == null) return;
        serviceOrders.add(service);
        System.out.println("Service '" + service.getServiceName() + "' ordered for room " + room.getRoomCode());
    }

    public double getTotalCost() {
        double total = 0;
        for (Service s : serviceOrders) total += s.getServiceCost();
        return total;
    }

    public boolean hasService(Service service) { return serviceOrders.contains(service); }
    public int getServiceCount() { return serviceOrders.size(); }

    public void displayOrders() {
        if (serviceOrders.isEmpty()) { System.out.println("No services ordered for room " + room.getRoomCode()); return; }
        System.out.println("Services for Room " + room.getRoomCode() + ":");
        for (Service s : serviceOrders) System.out.println("  - " + s.toString());
        System.out.printf("  Total: $%.2f%n", getTotalCost());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RoomService)) return false;
        RoomService rs = (RoomService) obj;
        return this.room.equals(rs.room);
    }

    @Override
    public int hashCode() { return room.hashCode(); }
}
