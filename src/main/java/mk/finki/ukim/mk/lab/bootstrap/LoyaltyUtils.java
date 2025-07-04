package mk.finki.ukim.mk.lab.bootstrap;

public class LoyaltyUtils {
    //GORJAN
    public static double getLoyaltyDiscount(Integer bookingCount) {
        if (bookingCount == null) {
            throw new IllegalArgumentException("Booking count cannot be null.");
        }
        if (bookingCount < 0) {
            throw new IllegalArgumentException("Booking count cannot be negative.");
        }

        if (bookingCount >= 50) return 0.5;
        else if (bookingCount >= 30) return 0.4;
        else if (bookingCount >= 20) return 0.3;
        else if (bookingCount >= 10) return 0.2;
        else if (bookingCount >= 5) return 0.1;
        else return 0.0;
    }
    //LEON
    public static String getLoyaltyLevel(int bookingCount) {
        if (bookingCount >= 50) return "GOLD";
        else if (bookingCount >= 30) return "GOLD";
        else if (bookingCount >= 10) return "SILVER";
        else return "REGULAR";
    }
}