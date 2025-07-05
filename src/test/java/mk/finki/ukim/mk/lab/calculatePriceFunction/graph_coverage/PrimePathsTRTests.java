package mk.finki.ukim.mk.lab.calculatePriceFunction.graph_coverage;

import mk.finki.ukim.mk.lab.model.Event; // adjust this to your actual class
import mk.finki.ukim.mk.lab.model.exceptions.InvalidDateForBookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrimePathsTRTests {
    private Event event;
    private LocalDateTime eventStart;
    private LocalDateTime earlyBooking;
    private LocalDateTime lateBooking;
    private LocalDateTime invalidBookingAfterEnd;
    private LocalDateTime invalidBookingAtStart;

    private static final double BASE_PRICE = 100.0;
    private static final int MAX_TICKETS = 100;
    private static final int HIGH_DEMAND_TICKETS = 89; //80 percent demand for the event
    private static final int LOW_DEMAND_TICKETS = 27;  //20 percent demand for the event
    private static final int TICKET_QUANTITY = 2;

    //setup function
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        eventStart = now.plusDays(40);//it makes the event start time to be after 40 days from now
        LocalDateTime eventEnd = eventStart.plusHours(3);//it makes the event end time to be after 3 hours of the start time
        earlyBooking = eventStart.minusDays(31);//30 days before the event start
        lateBooking = eventStart.minusDays(5);//30 days withing the event start
        invalidBookingAfterEnd = eventEnd.plusMinutes(1);//booking the event after it finished
        invalidBookingAtStart = eventStart;//booking the event after it is finished
        event = new Event(BASE_PRICE, MAX_TICKETS, eventStart, eventEnd);
    }// Path: [1,2,4,6,8,10,12,14,15,16,17,18]

    @DisplayName("Path 1 - Valid inputs with high demand and early booking apply both price multipliers")
    @Test
    void testPath1_HighDemand_EarlyBooking_BothMultipliers() {
        double price = event.calculatePrice(TICKET_QUANTITY, HIGH_DEMAND_TICKETS, earlyBooking);
        assertEquals(216.0, price);
    }

    // Path: [1,2,4,6,8,10,12,14,15,16,18]
    @DisplayName("Path 2 - Valid inputs with high demand but not early booking, only 1.2x applied")
    @Test
    void testPath2_HighDemand_LateBooking_OnlyHighDemandMultiplier() {
        double price = event.calculatePrice(TICKET_QUANTITY, HIGH_DEMAND_TICKETS, lateBooking);
        assertEquals(240.0, price);
    }

    // Path: [1,2,4,6,8,10,12,14,16,17,18]
    @DisplayName("Path 3 - Same as Path 1: double multipliers applied")
    @Test
    void testPath3_SameAsPath1_DoubleMultipliers() {
        double price = event.calculatePrice(TICKET_QUANTITY, HIGH_DEMAND_TICKETS, earlyBooking);
        assertEquals(216.0, price);
    }

    // Path: [1,2,4,6,8,10,12,14,16,18]
    @DisplayName("Path 4 - Valid inputs with low demand and early booking, only 0.9x multiplier applied")
    @Test
    void testPath4_LowDemand_EarlyBooking_OnlyEarlyBookingDiscount() {
        double price = event.calculatePrice(TICKET_QUANTITY, LOW_DEMAND_TICKETS, earlyBooking);
        assertEquals(180.0, price);
    }

    // Path: [1,2,4,6,8,10,12,13]
    @DisplayName("Path 5 - Valid inputs with low demand and no early booking, no multipliers applied")
    @Test
    void testPath5_LowDemand_NormalBooking_NoDiscounts() {
        double price = event.calculatePrice(TICKET_QUANTITY, LOW_DEMAND_TICKETS, lateBooking);
        assertEquals(200.0, price);
    }

    // Path: [1,2,4,6,8,10,11]
    @DisplayName("Path 6 - Valid inputs with demandRatio > 0.75 only, triggers 1.2x multiplier")
    @Test
    void testPath6_HighDemand_Only() {
        LocalDateTime veryLateBooking = eventStart.minusDays(1);
        double price = event.calculatePrice(TICKET_QUANTITY, HIGH_DEMAND_TICKETS, veryLateBooking);
        assertEquals(240.0, price);
    }

    // Path: [1,2,4,6,8,9]
    @DisplayName("Path 7 - Invalid: bookingTime is after endTime, should throw InvalidDateForBookingException")
    @Test
    void testPath7_BookingTimeAfterEvent_ThrowsException() {
        InvalidDateForBookingException exception = assertThrows(
                InvalidDateForBookingException.class,
                () -> event.calculatePrice(TICKET_QUANTITY, LOW_DEMAND_TICKETS, invalidBookingAfterEnd)
        );
        assertEquals("Booking time cannot be after the event has ended.", exception.getMessage());
    }

    // Path: [1,2,4,6,7]
    @DisplayName("Path 8 - Invalid: bookingTime is not before event start, should throw InvalidDateForBookingException")
    @Test
    void testPath8_BookingTimeOnOrAfterStart_ThrowsException() {
        InvalidDateForBookingException exception = assertThrows(
                InvalidDateForBookingException.class,
                () -> event.calculatePrice(TICKET_QUANTITY, LOW_DEMAND_TICKETS, invalidBookingAtStart)
        );
        assertEquals("Booking must be made before the event starts.", exception.getMessage());
    }

    // Path: [1,2,4,5]
    @DisplayName("Path 9 - Invalid: ticketsBookedSoFar is negative")
    @Test
    void testPath9_TicketsBookedSoFarInvalid_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(TICKET_QUANTITY, -1, earlyBooking)
        );
        assertEquals("Tickets booked so far must be zero or a positive integer.", exception.getMessage());
    }

    // Path: [1,2,3]
    @DisplayName("Path 10 - Invalid: numTickets is non-positive")
    @Test
    void testPath10_NumTicketsInvalid_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> event.calculatePrice(0, LOW_DEMAND_TICKETS, earlyBooking)
        );
        assertEquals("Number of tickets must be a positive integer.", exception.getMessage());
    }
}