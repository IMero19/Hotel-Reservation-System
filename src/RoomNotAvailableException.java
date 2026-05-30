public class RoomNotAvailableException extends Exception {
    private int roomCode;

    public RoomNotAvailableException(int roomCode) {
        super("Room " + roomCode + " is currently not available for booking.");
        this.roomCode = roomCode;
    }

    public int getRoomCode() { return roomCode; }
}
