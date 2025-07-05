package mk.finki.ukim.mk.lab.save_event.BaseTests;

import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveEventParameterizedTest {

    private EventRepository eventRepository;
    private LocationRepository locationRepository;
    private EventServiceImplementation service;
    private EventBookingRepository bookingRepository;

    @BeforeEach
    void setup() {
        eventRepository = mock(EventRepository.class);
        locationRepository = mock(LocationRepository.class);
        bookingRepository = mock(EventBookingRepository.class);
        service = new EventServiceImplementation(eventRepository, locationRepository,bookingRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    @DisplayName("Invalid event name using @ValueSource")
    void testInvalidEventName(String name) {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.save_event(null, name, "Description", 5.0, 1L,
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 50.0, 100)
        );
        assertEquals("Event name must not be null or blank.", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "null, Description",
            "'', Description",
            "'   ', Description",
            "ValidName, ''",
            "ValidName, null"
    })
    @DisplayName("Invalid name or description using @CsvSource")
    void testInvalidNameOrDescription(String name, String description) {
        name = "null".equals(name) ? null : name;
        description = "null".equals(description) ? null : description;

        String finalName = name;
        String finalDescription = description;
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.save_event(null, finalName, finalDescription, 5.0, 1L,
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 50.0, 100)
        );
        assertTrue(ex.getMessage().contains("must not be null or blank"));
    }

    enum ScoreType {
        NEGATIVE(-1.0), TOO_HIGH(11.0), VALID(5.0);

        final double score;
        ScoreType(double score) {
            this.score = score;
        }

        double getScore() {
            return score;
        }
    }

    @ParameterizedTest
    @EnumSource(value = ScoreType.class, names = { "NEGATIVE", "TOO_HIGH" })
    @DisplayName("Invalid popularityScore using @EnumSource")
    void testInvalidPopularityScore(ScoreType type) {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.save_event(null, "Event", "Description", type.getScore(), 1L,
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 50.0, 100)
        );
        assertEquals("Popularity score must be between 0 and 10.", ex.getMessage());
    }

    static Stream<Arguments> invalidDatePairs() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(null, now),
                Arguments.of(now, null),
                Arguments.of(now.plusHours(1), now) // from is after to
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDatePairs")
    @DisplayName("Invalid date pairs using @MethodSource")
    void testInvalidDateInputs(LocalDateTime from, LocalDateTime to) {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.save_event(null, "Event", "Description", 5.0, 1L,
                        from, to, 50.0, 100)
        );
        assertTrue(ex.getMessage().contains("date") || ex.getMessage().contains("before"));
    }
}
