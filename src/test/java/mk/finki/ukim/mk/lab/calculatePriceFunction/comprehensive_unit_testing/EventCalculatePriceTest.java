package mk.finki.ukim.mk.lab.calculatePriceFunction.comprehensive_unit_testing;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidDateForBookingException;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidRangeSetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventCalculatePriceTest {

    private Event event;
    private LocalDateTime now;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        eventStartTime = now.plusDays(60);
        eventEndTime = eventStartTime.plusHours(3);

        event = new Event();
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setBasePrice(100.0);
        event.setMaxTickets(100);
    }

    @Test
    @DisplayName("Low demand with early booking valid test")
    void testValidCalculationLowDemandEarlyBooking() {
        LocalDateTime bookingTime = eventStartTime.minusDays(35);
        double result = event.calculatePrice(2, 50, bookingTime);
        assertEquals(180.0, result, 0.01); // 100 * 0.9 (early discount) * 2 tickets
    }

    @Test
    @DisplayName("High demand with no early booking as valid test")
    void testValidCalculation_HighDemand_NoEarlyBooking() {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        double result = event.calculatePrice(1, 80, bookingTime);
        assertEquals(120.0, result, 0.01); // 100 * 1.2 (high demand) * 1 ticket
    }

    @Test
    @DisplayName("High demand with early booking as valid test")
    void testValidCalculationHighDemandEarlyBooking() {
        LocalDateTime bookingTime = eventStartTime.minusDays(35);
        double result = event.calculatePrice(3, 80, bookingTime);
        assertEquals(324.0, result, 0.01); // 100 * 1.2 * 0.9 * 3 tickets
    }

    @Test
    @DisplayName("Testing with normal price")
    void testValidCalculationNormalPrice() {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        double result = event.calculatePrice(1, 50, bookingTime);
        assertEquals(100.0, result, 0.01); // 100 * 1 ticket (no discounts/increases)
    }

    @Test
    @DisplayName("IllegalArgumentException when numTickets is null")
    void testNumTicketsNull() {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(null, 50, bookingTime)
        );
        assertEquals("Number of tickets must be a positive integer.", exception.getMessage());
    }

    @Test
    @DisplayName("IllegalArgumentException when ticketsBookedSoFar is null")
    void testTicketsBookedSoFarNull() {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(2, null, bookingTime)
        );
        assertEquals("Tickets booked so far must be zero or a positive integer.", exception.getMessage());
    }

    @Test
    @DisplayName("IllegalArgumentException when bookingTime is null")
    void testBookingTimeNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(2, 50, null)
        );
        assertEquals("Booking time cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("InvalidDateForBookingException when booking after event ends")
    void testBookingAfterEventEnds() {
        LocalDateTime bookingTime = eventEndTime.plusHours(1);
        InvalidDateForBookingException exception = assertThrows(
                InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, bookingTime)
        );
        assertEquals("Booking time cannot be after the event has ended.", exception.getMessage());
    }

    @Test
    @DisplayName("InvalidDateForBookingException when booking during event")
    void testBookingDuringEvent() {
        LocalDateTime bookingTime = eventStartTime.plusHours(1);
        InvalidDateForBookingException exception = assertThrows(
                InvalidDateForBookingException.class,
                () -> event.calculatePrice(2, 50, bookingTime)
        );
        assertEquals("Booking must be made before the event starts.", exception.getMessage());
    }

    @Test
    @DisplayName("InvalidRangeSetException when maxTickets is negative")
    void testMaxTicketsNegative() {
        event.setMaxTickets(-10);
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        InvalidRangeSetException exception = assertThrows(
                InvalidRangeSetException.class,
                () -> event.calculatePrice(2, 50, bookingTime)
        );
        assertEquals("Max tickets must be a positive integer.", exception.getMessage());
    }

    @Test
    @DisplayName("ArithmeticException when maxTickets is zero")
    void testMaxTicketsZero() {
        event.setMaxTickets(0);
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> event.calculatePrice(2, 50, bookingTime)
        );
        assertEquals("Dividing by zero!", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 0})
    @DisplayName("IllegalArgumentException for invalid numTickets values")
    void testInvalidNumTickets(int numTickets) {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);
        assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(numTickets, 50, bookingTime)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -5, -1})
    @DisplayName("Should throw IllegalArgumentException for negative ticketsBookedSoFar")
    void testNegativeTicketsBookedSoFar(int ticketsBooked) {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);

        assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(2, ticketsBooked, bookingTime)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    @DisplayName("Should calculate normal price for valid numTickets")
    void testValidNumTickets(int numTickets) {
        LocalDateTime bookingTime = eventStartTime.minusDays(15);

        double result = event.calculatePrice(numTickets, 50, bookingTime);

        assertEquals(100.0 * numTickets, result, 0.01);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 50, 15, 100.0",      // Normal price scenario
            "2, 50, 35, 180.0",      // Early booking discount scenario
            "1, 80, 15, 120.0",      // High demand price increase scenario
            "3, 80, 35, 324.0",      // High demand + early booking scenario
            "2, 0, 15, 200.0",       // Zero tickets booked scenario
            "1, 75, 15, 100.0",      // Exactly 75% demand (boundary) scenario
            "1, 76, 15, 120.0"       // Just over 75% demand scenario
    })
    @DisplayName("Should calculate correct price for various scenarios")
    void testPriceCalculationScenarios(int numTickets, int ticketsBooked, int daysBefore, double expectedPrice) {
        LocalDateTime bookingTime = eventStartTime.minusDays(daysBefore);

        double result = event.calculatePrice(numTickets, ticketsBooked, bookingTime);

        assertEquals(expectedPrice, result, 0.01);
    }

    @ParameterizedTest
    @CsvSource({
            "null, 50, 15, 'Number of tickets must be a positive integer.'",
            "2, null, 15, 'Tickets booked so far must be zero or a positive integer.'",
            "2, 50, null, 'Booking time cannot be null.'"
    })
    @DisplayName("Should throw IllegalArgumentException for null parameters")
    void testNullParameters(String numTicketsStr, String ticketsBookedStr, String daysBeforeStr, String expectedMessage) {
        Integer numTickets = "null".equals(numTicketsStr) ? null : Integer.parseInt(numTicketsStr);
        Integer ticketsBooked = "null".equals(ticketsBookedStr) ? null : Integer.parseInt(ticketsBookedStr);
        LocalDateTime bookingTime = "null".equals(daysBeforeStr) ? null : eventStartTime.minusDays(Integer.parseInt(daysBeforeStr));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(numTickets, ticketsBooked, bookingTime)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("providePriceCalculationTestData")
    @DisplayName("Should calculate price correctly using method source")
    void testPriceCalculationWithMethodSource(PriceTestDataDTO testData) {
        // Setup event with test data
        event.setBasePrice(testData.basePrice);
        event.setMaxTickets(testData.maxTickets);

        LocalDateTime bookingTime = eventStartTime.minusDays(testData.daysBefore);

        double result = event.calculatePrice(testData.numTickets, testData.ticketsBooked, bookingTime);

        assertEquals(testData.expectedPrice, result, 0.01, testData.description);
    }

    static Stream<PriceTestDataDTO> providePriceCalculationTestData() {
        return Stream.of(
                new PriceTestDataDTO(1, 50, 15, 100.0, 100, 100.0, "Normal price calculation"),
                new PriceTestDataDTO(2, 50, 35, 100.0, 100, 180.0, "Early booking discount applied"),
                new PriceTestDataDTO(3, 80, 35, 100.0, 100, 324.0, "High demand + early booking"),
                new PriceTestDataDTO(2, 25, 15, 50.0, 100, 100.0, "Different base price")
        );
    }


    @Test
    @DisplayName("Boundary test - exactly 75% demand")
    void testBoundaryDemandRatio() {
        double result = event.calculatePrice(1, 75, eventStartTime.minusDays(15));
        assertEquals(100.0, result, 0.01, "75% demand should not trigger price increase");
    }

    @Test
    @DisplayName("Boundary test - just over 75% demand")
    void testJustOverBoundaryDemandRatio() {
        double result = event.calculatePrice(1, 76, eventStartTime.minusDays(15));
        assertEquals(120.0, result, 0.01, "76% demand should trigger price increase");
    }


    @Test
    @DisplayName("Boundary test - just under 30 days early booking")
    void testJustUnderBoundaryEarlyBooking() {
        double result = event.calculatePrice(1, 50, eventStartTime.minusDays(29));
        assertEquals(100.0, result, 0.01, "29 days should not trigger early booking discount");
    }

    static class PriceTestDataDTO {
        final int numTickets;
        final int ticketsBooked;
        final int daysBefore;
        final double basePrice;
        final int maxTickets;
        final double expectedPrice;
        final String description;

        PriceTestDataDTO(int numTickets, int ticketsBooked, int daysBefore,
                         double basePrice, int maxTickets, double expectedPrice, String description) {
            this.numTickets = numTickets;
            this.ticketsBooked = ticketsBooked;
            this.daysBefore = daysBefore;
            this.basePrice = basePrice;
            this.maxTickets = maxTickets;
            this.expectedPrice = expectedPrice;
            this.description = description;
        }
    }

}