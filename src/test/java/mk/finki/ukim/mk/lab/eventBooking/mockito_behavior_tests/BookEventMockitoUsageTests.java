package mk.finki.ukim.mk.lab.eventBooking.mockito_behavior_tests;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventBookingServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookEventMockitoUsageTests {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventBookingRepository eventBookingRepository;

    @InjectMocks
    private EventBookingServiceImplementation eventBookingService;

    private Event mockEvent;
    private EventBooking mockBooking;
    private static final Long VALID_EVENT_ID = 1L;
    private static final String VALID_ATTENDEE_NAME = "Leon Asanovski";
    private static final String VALID_ATTENDEE_ADDRESS = "Boulevard of broken dreams";
    private static final Long VALID_NUM_TICKETS = 2L;
    private static final double BASE_PRICE = 100.0;
    private static final int MAX_TICKETS = 100;
    private static final int ALREADY_BOOKED = 20;
    private static final LocalDateTime START_TIME = LocalDateTime.now().plusDays(30);
    private static final LocalDateTime END_TIME = LocalDateTime.now().plusDays(30).plusHours(3);

    @BeforeEach
    void setUp() {
        mockEvent = new Event();
        mockEvent.setId(VALID_EVENT_ID);
        mockEvent.setBasePrice(BASE_PRICE);
        mockEvent.setMaxTickets(MAX_TICKETS);
        mockEvent.setStartTime(START_TIME);
        mockEvent.setEndTime(END_TIME);
        mockBooking = new EventBooking(mockEvent, VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_NUM_TICKETS, 200.0);
    }
    @Test
    @DisplayName("Retrieving information from Event Object and Setting attribute values to Event Object")
    void testGettersAndSetters() {
        Event event = mockEvent;
        assertEquals(VALID_EVENT_ID, event.getId());
        assertEquals(BASE_PRICE, event.getBasePrice());
        assertEquals(MAX_TICKETS, event.getMaxTickets());
        assertEquals(START_TIME, event.getStartTime());
        assertEquals(END_TIME, event.getEndTime());
        Event newEvent = new Event();
        Long newEventId = 999L;
        double newBasePrice = 200.0;
        int newMaxTickets = 500;
        LocalDateTime newStartTime = LocalDateTime.of(2025, 10, 1, 18, 0);
        LocalDateTime newEndTime = LocalDateTime.of(2025, 10, 1, 21, 0);
        newEvent.setId(newEventId);
        newEvent.setBasePrice(newBasePrice);
        newEvent.setMaxTickets(newMaxTickets);
        newEvent.setStartTime(newStartTime);
        newEvent.setEndTime(newEndTime);
        assertEquals(newEventId, newEvent.getId());
        assertEquals(newBasePrice, newEvent.getBasePrice());
        assertEquals(newMaxTickets, newEvent.getMaxTickets());
        assertEquals(newStartTime, newEvent.getStartTime());
        assertEquals(newEndTime, newEvent.getEndTime());
    }

    @Test
    @DisplayName("Successfully booking event with valid parameters")
    void testBookEvent_Success() {
        //mocking behavior of event and eventBooking repos
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);

        EventBooking result = eventBookingService.bookEvent(
                VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
        assertNotNull(result);
        assertEquals(mockBooking, result);
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(eventBookingRepository).countTicketsByEventId(VALID_EVENT_ID);
        verify(eventBookingRepository).save(any(EventBooking.class));
    }

    @Test
    @DisplayName("Should create booking with correct parameters")
    void testBookEvent_CorrectBookingCreation() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenAnswer(invocation -> {
            EventBooking booking = invocation.getArgument(0);
            assertEquals(mockEvent, booking.getEvent());
            assertEquals(VALID_ATTENDEE_NAME, booking.getAttendeeName());
            assertEquals(VALID_ATTENDEE_ADDRESS, booking.getAttendeeAddress());
            assertEquals(VALID_NUM_TICKETS, booking.getNumberOfTickets());
            assertTrue(booking.getTotalPrice() > 0);
            return booking;
        });
        eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
    }


    @ParameterizedTest
    @CsvSource({
            "Gorjan Bogoevski, Ulica Skopska, 1, 1",
            "Leon Asanovski, Ulica Cairska, 2, 3",
            "Sara Asanovska, Ulica Cairska, 3, 5",
            "Emilija Bogoevska, Ulica Skopska, 4, 10"
    })
    @DisplayName("Should successfully book with CSV valid parameters")
    void testBookEvent_VariousValidParameters(String name, String address, Long eventId, Long numTickets) {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(eventId)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        EventBooking result = eventBookingService.bookEvent(name, address, eventId, numTickets);
        assertNotNull(result);
        verify(eventRepository).findById(eventId);
        verify(eventBookingRepository).countTicketsByEventId(eventId);
        verify(eventBookingRepository).save(any(EventBooking.class));
    }

    static Stream<Arguments> validBookingData() {
        return Stream.of(
                Arguments.of("Gorjan Bogoevski", "Ulica Skopska", 1L, 1L),
                Arguments.of("Leon Asanovski", "Ulica Cairska", 2L, 2L),
                Arguments.of("Sara Asanovska", "Ulica Cairska", 3L, 5L),
                Arguments.of("Emilija Bogoevska", "Ulica Skopska", 4L, 10L)
        );
    }

    @ParameterizedTest
    @MethodSource("validBookingData")
    @DisplayName("Should successfully book with METHOD source data")
    void testBookEvent_MethodSourceData(String name, String address, Long eventId, Long numTickets) {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(eventId)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        EventBooking result = eventBookingService.bookEvent(name, address, eventId, numTickets);
        assertNotNull(result);
        assertEquals(mockBooking, result);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 5L, 10L, 50L, 100L})
    @DisplayName("Should successfully book with various ticket quantities")
    void testBookEvent_VariousTicketQuantities(Long numTickets) {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        EventBooking result = eventBookingService.bookEvent(
                VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, numTickets);
        assertNotNull(result);
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(eventBookingRepository).save(any(EventBooking.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n", "  \t  \n  "})
    @DisplayName("Should throw exception for invalid attendee names")
    void testBookEvent_InvalidAttendeeName(String invalidName) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(invalidName, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS));

        assertEquals("Attendee name cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n", "  \t  \n  "})
    @DisplayName("Should throw exception for invalid attendee addresses")
    void testBookEvent_InvalidAttendeeAddress(String invalidAddress) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, invalidAddress, VALID_EVENT_ID, VALID_NUM_TICKETS));
        assertEquals("Attendee address cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }

    @Test
    @DisplayName("Should throw exception for null event ID")
    void testBookEvent_NullEventId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, null, VALID_NUM_TICKETS));
        assertEquals("Event ID cannot be null.", exception.getMessage());
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -5L, -100L})
    @DisplayName("Should throw exception for invalid ticket numbers")
    void testBookEvent_InvalidNumTickets(Long invalidTickets) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, invalidTickets));
        assertEquals("Number of tickets must be a positive number.", exception.getMessage());
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }

    @Test
    @DisplayName("Should throw exception for null number of tickets")
    void testBookEvent_NullNumTickets() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, null));
        assertEquals("Number of tickets must be a positive number.", exception.getMessage());
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }


    @Test
    @DisplayName("Should throw exception when event is not found")
    void testBookEvent_EventNotFound() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS));
        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository).findById(VALID_EVENT_ID);
        verifyNoInteractions(eventBookingRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, 1000L, -1L, 0L})
    @DisplayName("Should throw RuntimeException for various non-existent event IDs")
    void testBookEvent_VariousNonExistentEventIds(Long nonExistentId) {
        when(eventRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, nonExistentId, VALID_NUM_TICKETS));
        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should call repositories in correct order")
    void testBookEvent_RepositoryInteractionOrder() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
        var inOrder = inOrder(eventRepository, eventBookingRepository);
        inOrder.verify(eventRepository).findById(VALID_EVENT_ID);
        inOrder.verify(eventBookingRepository).countTicketsByEventId(VALID_EVENT_ID);
        inOrder.verify(eventBookingRepository).save(any(EventBooking.class));
    }

    @Test
    @DisplayName("Should pass correct parameters to calculatePrice method")
    void testBookEvent_CalculatePriceParameters() {
        Event spyEvent = spy(mockEvent);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(spyEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
        verify(spyEvent).calculatePrice(eq(VALID_NUM_TICKETS.intValue()), eq(ALREADY_BOOKED), any(LocalDateTime.class));
    }


    @Test
    @DisplayName("Should handle zero already booked tickets")
    void testBookEvent_ZeroAlreadyBooked() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(0);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        EventBooking result = eventBookingService.bookEvent(
                VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
        assertNotNull(result);
        verify(eventBookingRepository).countTicketsByEventId(VALID_EVENT_ID);
    }


    @Test
    @DisplayName("Should verify all mock interactions")
    void testBookEvent_MockInteractions() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventBookingRepository.countTicketsByEventId(VALID_EVENT_ID)).thenReturn(ALREADY_BOOKED);
        when(eventBookingRepository.save(any(EventBooking.class))).thenReturn(mockBooking);
        eventBookingService.bookEvent(VALID_ATTENDEE_NAME, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS);
        verify(eventRepository, times(1)).findById(VALID_EVENT_ID);
        verify(eventBookingRepository, times(1)).countTicketsByEventId(VALID_EVENT_ID);
        verify(eventBookingRepository, times(1)).save(any(EventBooking.class));
        verifyNoMoreInteractions(eventRepository, eventBookingRepository);
    }

    @Test
    @DisplayName("Should not save booking if validation fails")
    void testBookEvent_NoSaveOnValidationFailure() {
        assertThrows(IllegalArgumentException.class,
                () -> eventBookingService.bookEvent(null, VALID_ATTENDEE_ADDRESS, VALID_EVENT_ID, VALID_NUM_TICKETS));
        verifyNoInteractions(eventRepository, eventBookingRepository);
    }
}