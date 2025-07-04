package mk.finki.ukim.mk.lab.getLoyaltyDiscount.GC;

import mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoyaltyDiscountPrimePathTest {

    @Test
    void testPath_1_2_3_nullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            LoyaltyUtils.getLoyaltyDiscount(null);
        });
    }

    @Test
    void testPath_1_2_4_5_negativeInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            LoyaltyUtils.getLoyaltyDiscount(-2);
        });
    }

    @Test
    void testPath_1_2_4_6_7_input60() {
        assertEquals(0.5, LoyaltyUtils.getLoyaltyDiscount(60));
    }

    @Test
    void testPath_1_2_4_6_8_9_input40() {
        assertEquals(0.4, LoyaltyUtils.getLoyaltyDiscount(40));
    }

    @Test
    void testPath_1_2_4_6_8_10_11_input25() {
        assertEquals(0.3, LoyaltyUtils.getLoyaltyDiscount(25));
    }

    @Test
    void testPath_1_2_4_6_8_10_12_13_input15() {
        assertEquals(0.2, LoyaltyUtils.getLoyaltyDiscount(15));
    }

    @Test
    void testPath_1_2_4_6_8_10_12_14_15_input7() {
        assertEquals(0.1, LoyaltyUtils.getLoyaltyDiscount(7));
    }

    @Test
    void testPath_1_2_4_6_8_10_12_14_16_input3() {
        assertEquals(0.0, LoyaltyUtils.getLoyaltyDiscount(3));
    }
}