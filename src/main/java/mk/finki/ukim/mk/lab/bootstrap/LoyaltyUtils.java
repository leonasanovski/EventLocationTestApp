package mk.finki.ukim.mk.lab.bootstrap;

public class LoyaltyUtils {
    //GORJAN
    public static double getLoyaltyDiscount(int bookingCount) {
        if (bookingCount >= 50) return 0.5;
        else if (bookingCount >= 30) return 0.4;
        else if (bookingCount >= 20) return 0.3;
        else if (bookingCount >= 10) return 0.2;
        else if (bookingCount >= 5) return 0.1;
        else return 0.0;
    }

    //LEON
    public static String getLoyaltyLevel(Integer bookingCount) {
        if (bookingCount == null || bookingCount < 0) {
            throw new IllegalArgumentException("Invalid booking count value!");
        }
        if (bookingCount >= 50)
            return "GOLD";
        else if (bookingCount >= 30)
            return "SILVER";
        else if (bookingCount >= 10)
            return "BRONZE";
        else return "REGULAR";
    }
}