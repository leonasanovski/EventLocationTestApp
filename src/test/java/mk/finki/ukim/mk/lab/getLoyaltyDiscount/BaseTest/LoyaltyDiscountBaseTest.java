package mk.finki.ukim.mk.lab.getLoyaltyDiscount.BaseTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyDiscountBaseTest {

    static class YourClass {
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
    }


    @ParameterizedTest
    @ValueSource(ints = {3, 7, 15, 25, 35, 60})
    void testLoyaltyDiscount_ValueSource(int bookingCount) {
        assertDoesNotThrow(() -> YourClass.getLoyaltyDiscount(bookingCount));
    }

    // 2. CsvSource
    @ParameterizedTest
    @CsvSource({
            "3, 0.0",
            "7, 0.1",
            "15, 0.2",
            "25, 0.3",
            "35, 0.4",
            "60, 0.5"
    })
    void testLoyaltyDiscount_CsvSource(int bookingCount, double expected) {
        assertEquals(expected, YourClass.getLoyaltyDiscount(bookingCount), 0.0001);
    }

    // 3. EnumSource
    enum BookingLevel {
        LOW(3), MEDIUM(25), HIGH(60);
        final int value;
        BookingLevel(int v) { this.value = v; }
    }

    @ParameterizedTest
    @EnumSource(BookingLevel.class)
    void testLoyaltyDiscount_EnumSource(BookingLevel level) {
        assertDoesNotThrow(() -> YourClass.getLoyaltyDiscount(level.value));
    }

    // 4. MethodSource
    static Stream<Arguments> loyaltyDiscountInputs() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(-5, true),
                Arguments.of(3, false),
                Arguments.of(50, false)
        );
    }

    @ParameterizedTest
    @MethodSource("loyaltyDiscountInputs")
    void testLoyaltyDiscount_MethodSource(Integer input, boolean expectException) {
        Executable exec = () -> YourClass.getLoyaltyDiscount(input);
        if (expectException) {
            assertThrows(IllegalArgumentException.class, exec);
        } else {
            assertDoesNotThrow(exec);
        }
    }
}