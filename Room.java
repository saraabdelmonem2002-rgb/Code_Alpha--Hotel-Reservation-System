package hotelreservationsystem;

import java.util.Objects;
public class Room {
    private final String id;
    private final RoomCategory category;
    private final double pricePerNight;

    public Room(String id, RoomCategory category, double pricePerNight) {
        this.id = id;
        this.category = category;
        this.pricePerNight = pricePerNight;
    }

    public String getId() { return id; }
    public RoomCategory getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }

    @Override
    public String toString() {
        return String.format("Room{id='%s', category=%s, pricePerNight=%.2f}", id, category, pricePerNight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

