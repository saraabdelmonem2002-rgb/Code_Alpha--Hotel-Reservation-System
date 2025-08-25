package hotelreservationsystem;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
public class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Storage storage;

    public Hotel(Storage storage) {
        this.storage = storage;
        loadInitialRooms();
        bookings.addAll(storage.loadAllBookings());
    }

    private void loadInitialRooms() {
        rooms.add(new Room("R101", RoomCategory.STANDARD, 30.0));
        rooms.add(new Room("R102", RoomCategory.STANDARD, 30.0));
        rooms.add(new Room("R201", RoomCategory.DELUXE, 50.0));
        rooms.add(new Room("R202", RoomCategory.DELUXE, 55.0));
        rooms.add(new Room("R301", RoomCategory.SUITE, 100.0));
    }

    public List<Room> searchAvailableRooms(RoomCategory category, LocalDate checkIn, LocalDate checkOut) {
        return rooms.stream()
                .filter(r -> r.getCategory() == category)
                .filter(r -> isRoomAvailable(r.getId(), checkIn, checkOut))
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        for (Booking b : bookings) {
            if (!b.getRoomId().equals(roomId)) continue;
            if (b.getStatus().equalsIgnoreCase("CANCELLED")) continue;
            if (b.getCheckIn().isBefore(checkOut) && checkIn.isBefore(b.getCheckOut())) {
                return false;
            }
        }
        return true;
    }

    public Booking makeBooking(String roomId, String guestName, LocalDate checkIn, LocalDate checkOut) throws Exception {
        Room room = rooms.stream().filter(r -> r.getId().equals(roomId)).findFirst()
                .orElseThrow(() -> new Exception("Room not found: " + roomId));
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            throw new Exception("Room is not available for selected dates.");
        }
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) throw new Exception("Check-out must be after check-in.");
        double amount = nights * room.getPricePerNight();
        Booking booking = new Booking(roomId, room.getCategory(), guestName, checkIn, checkOut, amount);


        boolean paid = PaymentSimulator.processPayment("Simulated", amount);
        if (!paid) throw new Exception("Payment failed during simulation.");

        bookings.add(booking);
        storage.saveBooking(booking);
        return booking;
    }

    public boolean cancelBooking(String bookingId) throws Exception {
        Optional<Booking> maybe = bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst();
        if (!maybe.isPresent()) {
            return false;
        }
        Booking b = maybe.get();
        if (b.getStatus().equalsIgnoreCase("CANCELLED")) return false;
        b.setStatus("CANCELLED");

        storage.overwriteAllBookings(bookings);
        return true;
    }

    public Optional<Booking> findBookingById(String bookingId) {
        return bookings.stream().filter(b -> b.getBookingId().equals(bookingId)).findFirst();
    }

    public List<Booking> listAllBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }
}