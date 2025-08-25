package hotelreservationsystem;

public class PaymentSimulator {
    public static boolean processPayment(String method, double amount) {
        System.out.printf("Simulating payment using %s for amount %.2f ...\n", method, amount);

        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {}
        if (amount > 0) {
            System.out.println("Payment simulated: SUCCESS");
            return true;
        } else {
            System.out.println("Payment simulated: FAILED (invalid amount)");
            return false;
        }
    }
}

