package mk.finki.ukim.mk.lab.calculatePriceFunction.ISP_base_coverage_tests;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidDateForBookingException;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidRangeSetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidArgumentException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventPriceCalculationTest {

    private Event event;
    private LocalDateTime now;
    private Location location;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        location = new Location(); // You may need to initialize with ID/name if needed

        event = new Event(
                "Test Event", "Description", 5.0, location,
                now.plusDays(20),              // startTime
                now.plusDays(20).plusHours(2), // endTime
                100.0,                         // basePrice
                100                             // maxTickets
        );

    }
    // 1. Base Case - Valid
    @Test
    void test1_validPrice() {
        double result = event.calculatePrice(2, 50, now.plusDays(10));
        assertEquals(200.0, result);
    }

    // 2. numTickets = null
    @Test
    void test2_nullNumTickets() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(null, 50, now.plusDays(10)));
    }

    // 3. numTickets < 0
    @Test
    void test3_negativeNumTickets() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(-5, 50, now.plusDays(10)));
    }

    // 4. numTickets = 0
    @Test
    void test4_zeroNumTickets() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(0, 50, now.plusDays(10)));
    }

    // 5. ticketsBookedSoFar = null
    @Test
    void test5_nullTicketsBooked() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(2, null, now.plusDays(10)));
    }

    // 6. ticketsBookedSoFar < 0
    @Test
    void test6_negativeTicketsBooked() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(2, -1, now.plusDays(10)));
    }

    // 7. ticketsBookedSoFar = 0
    @Test
    void test7_zeroTicketsBooked() {
        double result = event.calculatePrice(2, 0, now.plusDays(10));
        assertEquals(200.0, result);
    }

    // 8. bookingTime = null
    @Test
    void test8_nullBookingTime() {
        assertThrows(IllegalArgumentException.class,
                () -> event.calculatePrice(2, 50, null));
    }

    // 9. bookingTime > endTime
    @Test
    void test9_bookingAfterEndTime() {
        assertThrows(InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, now.plusDays(25)));
    }

    // 10. bookingTime = startTime
    @Test
    void test10_bookingAtStartTime() {
        assertThrows(InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, event.getStartTime()));
    }

    // 11. Early bird discount (more than 30 days before start)
    @Test
    void test11_earlyBirdDiscount() {
        event.setStartTime(now.plusDays(50));
        event.setEndTime(now.plusDays(50).plusHours(2));
        double result = event.calculatePrice(2, 50, now.plusDays(10));
        assertEquals(180.0, result); // 100 * 0.9 * 2
    }

    // 12. Booking between start and end (not allowed)
    @Test
    void test12_bookingDuringEvent() {
        assertThrows(InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, event.getStartTime().plusMinutes(10)));
    }

    // 13. maxTickets = 0 → division by zero
    @Test
    void test13_maxTicketsZero() {
        event.setMaxTickets(0);
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> event.calculatePrice(2, 50, now.plusDays(10)));
        assertEquals("Dividing by zero!", ex.getMessage());
    }

    // 14. maxTickets < 0 (undefined behavior)
    @Test
    void test14_maxTicketsNegative() {
        event.setMaxTickets(-10);
        InvalidRangeSetException ex = assertThrows(InvalidRangeSetException.class,
                () -> event.calculatePrice(2, 50, now.plusDays(10)));
        assertEquals("Max tickets must be a positive integer.", ex.getMessage());
    }

    // 15. endTime < now but bookingTime is still before endTime
    @Test
    void test15_eventInPast_bookingValid() {
        event.setEndTime(now.minusDays(1));
        event.setStartTime(now.minusDays(2));
        double result = event.calculatePrice(2, 50, now.minusDays(3));
        assertEquals(200.0, result);
    }

    // 16. endTime = now (still acceptable if bookingTime is before)
    @Test
    void test16_endTimeEqualsNow() {
        event.setEndTime(now);
        event.setStartTime(now.plusHours(1));
        double result = event.calculatePrice(2, 50, now.minusHours(1));
        assertEquals(200.0, result);
    }

    // 17. startTime < now → booking not allowed
    @Test
    void test17_eventAlreadyStarted() {
        event.setStartTime(now.minusHours(1));
        assertThrows(InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, now));
    }

    // 18. startTime = now → booking not allowed
    @Test
    void test18_startTimeIsNow() {
        event.setStartTime(now);
        assertThrows(InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, now));
    }

    // 19. basePrice = 0 → return 0
    @Test
    void test19_zeroBasePrice() {
        event.setBasePrice(0);
        double result = event.calculatePrice(2, 50, now.plusDays(10));
        assertEquals(0.0, result);
    }

    // 20. basePrice = -50 → negative return value (should not happen)
    @Test
    void test20_negativeBasePrice() {
        event.setBasePrice(-50);
        double result = event.calculatePrice(2, 50, now.plusDays(10));
        assertTrue(result < 0);
    }

    // 21. Very large return value
    @Test
    void test21_veryLargeReturn() {
        event.setBasePrice(Double.MAX_VALUE / 10);
        double result = event.calculatePrice(10, 50, now.plusDays(10));
        assertTrue(result >= Double.MAX_VALUE / 1.5);
    }
}


