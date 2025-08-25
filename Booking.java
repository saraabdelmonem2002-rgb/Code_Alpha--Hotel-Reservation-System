package hotelreservationsystem;

import java.time.LocalDate;
import java.util.UUID;
public class Booking {
    private final String bookingId;
    private final String roomId;
    private final RoomCategory category;
    private final String guestName;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double amount;
    private String status;

    public Booking(String bookingId, String roomId, RoomCategory category, String guestName,
                   LocalDate checkIn, LocalDate checkOut, double amount, String status) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.category = category;
        this.guestName = guestName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.amount = amount;
        this.status = status;
    }

    public Booking(String roomId, RoomCategory category, String guestName,
                   LocalDate checkIn, LocalDate checkOut, double amount) {
        this(UUID.randomUUID().toString(), roomId, category, guestName, checkIn, checkOut, amount, "CONFIRMED");
    }

    public String getBookingId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public RoomCategory getCategory() { return category; }
    public String getGuestName() { return guestName; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", category=" + category +
                ", guestName='" + guestName + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }


    public String toCSV() {
        return String.join(",",
                bookingId,
                roomId,
                category.name(),
                escapeCSV(guestName),
                checkIn.toString(),
                checkOut.toString(),
                String.format("%.2f", amount),
                status
        );
    }

    private String escapeCSV(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    public static Booking fromCSV(String line) {
        String[] parts = CSVUtil.splitCSV(line);
        String bookingId = parts[0];
        String roomId = parts[1];
        RoomCategory category = RoomCategory.valueOf(parts[2]);
        String guestName = parts[3];
        LocalDate checkIn = LocalDate.parse(parts[4]);
        LocalDate checkOut = LocalDate.parse(parts[5]);
        double amount = Double.parseDouble(parts[6]);
        String status = parts[7];
        return new Booking(bookingId, roomId, category, guestName, checkIn, checkOut, amount, status);
    }
}

