package mk.finki.ukim.mk.lab.getLoyaltyLevel.isp_base_coverage_testing;

import org.junit.jupiter.api.Test;

import static mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils.getLoyaltyLevel;
import static org.junit.jupiter.api.Assertions.*;

public class LoyaltyLevelTest {
    // 1. C1B (bookingCount > 0) + C2A (GOLD)
    @Test
    void testBookingCount1() {
        assertEquals("GOLD", getLoyaltyLevel(60));
    }

    // 2. C1A (bookingCount < 0) + C2A (Exception expected)
    @Test
    void testBookingCount2() {
        assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(-5));
    }

    // 3. C1C (bookingCount == 0) + C2A (should return REGULAR)
    @Test
    void testBookingCount3() {
        assertEquals("REGULAR", getLoyaltyLevel(0));
    }

    // 4. C1D (bookingCount == null) + C2A (Exception expected)
    @Test
    void testBookingCount4() {
        assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(null));
    }

    // 5. C1B (bookingCount > 0) + C2B (SILVER)
    @Test
    void testBookingCount5() {
        assertEquals("SILVER", getLoyaltyLevel(35));
    }

    // 6. C1B (bookingCount > 0) + C2C (BRONZE)
    @Test
    void testBookingCount6() {
        assertEquals("BRONZE", getLoyaltyLevel(20));
    }

    // 7. C1B (bookingCount > 0) + C2D (REGULAR)
    @Test
    void testBookingCount7() {
        assertEquals("REGULAR", getLoyaltyLevel(5));
    }
}
