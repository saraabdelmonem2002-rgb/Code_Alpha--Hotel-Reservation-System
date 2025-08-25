package hotelreservationsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public class MainGUI {
    private static final String BOOKINGS_FILE = "bookings.csv";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Storage storage = new Storage(BOOKINGS_FILE);
            Hotel hotel = new Hotel(storage);
            new HotelGUI(hotel);
        });
    }
}

class HotelGUI extends JFrame {
    private final Hotel hotel;

    public HotelGUI(Hotel hotel) {
        this.hotel = hotel;
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new GridLayout(7, 1, 10, 10));

        JButton btnSearch = new JButton("Search Available Rooms");
        JButton btnBook = new JButton("Book Room");
        JButton btnCancel = new JButton("Cancel Booking");
        JButton btnView = new JButton("View Booking");
        JButton btnListBookings = new JButton("List All Bookings");
        JButton btnListRooms = new JButton("List All Rooms");
        JButton btnExit = new JButton("Exit");

        add(btnSearch);
        add(btnBook);
        add(btnCancel);
        add(btnView);
        add(btnListBookings);
        add(btnListRooms);
        add(btnExit);

        btnSearch.addActionListener(e -> searchRooms());
        btnBook.addActionListener(e -> bookRoom());
        btnCancel.addActionListener(e -> cancelBooking());
        btnView.addActionListener(e -> viewBooking());
        btnListBookings.addActionListener(e -> listBookings());
        btnListRooms.addActionListener(e -> listRooms());
        btnExit.addActionListener(e -> System.exit(0));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private RoomCategory chooseCategory() {
        String[] options = {"STANDARD", "DELUXE", "SUITE"};
        String choice = (String) JOptionPane.showInputDialog(
                this, "Choose Room Category:",
                "Room Category", JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choice == null) return null;
        return RoomCategory.valueOf(choice);
    }

    private LocalDate[] chooseDates() {
        try {
            String checkInStr = JOptionPane.showInputDialog(this, "Enter Check-In Date (YYYY-MM-DD):");
            String checkOutStr = JOptionPane.showInputDialog(this, "Enter Check-Out Date (YYYY-MM-DD):");
            if (checkInStr == null || checkOutStr == null) return null;
            LocalDate checkIn = LocalDate.parse(checkInStr.trim());
            LocalDate checkOut = LocalDate.parse(checkOutStr.trim());
            return new LocalDate[]{checkIn, checkOut};
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void searchRooms() {
        RoomCategory cat = chooseCategory();
        if (cat == null) return;
        LocalDate[] dates = chooseDates();
        if (dates == null) return;

        List<Room> avail = hotel.searchAvailableRooms(cat, dates[0], dates[1]);
        if (avail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available rooms for these dates.", "Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Available Rooms:\n");
            for (Room r : avail) sb.append(r).append("\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Rooms", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void bookRoom() {
        RoomCategory cat = chooseCategory();
        if (cat == null) return;
        LocalDate[] dates = chooseDates();
        if (dates == null) return;

        List<Room> avail = hotel.searchAvailableRooms(cat, dates[0], dates[1]);
        if (avail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available rooms for these dates.", "Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] roomIds = avail.stream().map(Room::getId).toArray(String[]::new);
        String roomId = (String) JOptionPane.showInputDialog(
                this, "Select Room ID:", "Choose Room",
                JOptionPane.QUESTION_MESSAGE, null, roomIds, roomIds[0]);
        if (roomId == null) return;

        String guestName = JOptionPane.showInputDialog(this, "Enter Guest Name:");
        if (guestName == null) return;

        try {
            Booking booking = hotel.makeBooking(roomId, guestName, dates[0], dates[1]);
            JOptionPane.showMessageDialog(this, "Booking Successful!\n" + booking, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        String bookingId = JOptionPane.showInputDialog(this, "Enter Booking ID to cancel:");
        if (bookingId == null) return;
        try {
            boolean ok = hotel.cancelBooking(bookingId);
            JOptionPane.showMessageDialog(this, ok ? "Booking Cancelled." : "Booking not found or already cancelled.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewBooking() {
        String bookingId = JOptionPane.showInputDialog(this, "Enter Booking ID:");
        if (bookingId == null) return;
        Optional<Booking> maybe = hotel.findBookingById(bookingId);
        if (maybe.isPresent()) {
            JOptionPane.showMessageDialog(this, maybe.get().toString(), "Booking Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Booking not found.");
        }
    }

    private void listBookings() {
        List<Booking> all = hotel.listAllBookings();
        if (all.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found.");
            return;
        }
        JTextArea text = new JTextArea();
        for (Booking b : all) text.append(b.toString() + "\n");
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scroll, "All Bookings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void listRooms() {
        List<Room> rooms = hotel.getAllRooms();
        JTextArea text = new JTextArea();
        for (Room r : rooms) text.append(r.toString() + "\n");
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scroll, "All Rooms", JOptionPane.INFORMATION_MESSAGE);
    }
}

