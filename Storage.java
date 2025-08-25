package hotelreservationsystem;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
public class Storage {
    private final Path bookingsFile;

    public Storage(String bookingsFilePath) {
        this.bookingsFile = Paths.get(bookingsFilePath);
        try {
            if (!Files.exists(bookingsFile)) {
                Files.createFile(bookingsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage: " + e.getMessage(), e);
        }
    }

    public synchronized void saveBooking(Booking booking) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(bookingsFile, StandardOpenOption.APPEND)) {
            w.write(booking.toCSV());
            w.newLine();
        }
    }

    public synchronized List<Booking> loadAllBookings() {
        try {
            return Files.lines(bookingsFile)
                    .filter(line -> !line.trim().isEmpty())
                    .map(Booking::fromCSV)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Failed to load bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public synchronized void overwriteAllBookings(List<Booking> bookings) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(bookingsFile, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Booking b : bookings) {
                w.write(b.toCSV());
                w.newLine();
            }
        }
    }
}

