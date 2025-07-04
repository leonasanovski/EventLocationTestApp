package mk.finki.ukim.mk.lab.getLoyaltyDiscount.ISP;

import mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoyaltyDiscountTest {

    @Test
    void testBookingCountBetween5And9_Returns01() {
        assertEquals(0.1, LoyaltyUtils.getLoyaltyDiscount(5));
    }

    @Test
    void testBookingCountLessThanZero_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            LoyaltyUtils.getLoyaltyDiscount(-1);
        });
    }

    @Test
    void testBookingCountNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            LoyaltyUtils.getLoyaltyDiscount(null);
        });
    }

    @Test
    void testBookingCountBetween1And4_Returns0() {
        assertEquals(0.0, LoyaltyUtils.getLoyaltyDiscount(3));
    }

    @Test
    void testBookingCountBetween10And19_Returns02() {
        assertEquals(0.2, LoyaltyUtils.getLoyaltyDiscount(15));
    }

    @Test
    void testBookingCountBetween20And29_Returns03() {
        assertEquals(0.3, LoyaltyUtils.getLoyaltyDiscount(25));
    }

    @Test
    void testBookingCountBetween30And49_Returns04() {
        assertEquals(0.4, LoyaltyUtils.getLoyaltyDiscount(40));
    }

    @Test
    void testBookingCountGreaterThanOrEqualTo50_Returns05() {
        assertEquals(0.5, LoyaltyUtils.getLoyaltyDiscount(60));
    }
}