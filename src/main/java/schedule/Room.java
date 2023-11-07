package schedule;

public record Room(String name, int capacity) {
    public Room copy() {
        return new Room(name, capacity);
    }
}
