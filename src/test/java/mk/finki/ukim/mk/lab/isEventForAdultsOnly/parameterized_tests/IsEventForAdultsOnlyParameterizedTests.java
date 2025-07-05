package mk.finki.ukim.mk.lab.isEventForAdultsOnly.parameterized_tests;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class IsEventForAdultsOnlyParameterizedTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImplementation eventService;

    private Event event;
    private Location location;

    @BeforeEach
    void setup() {
        event = new Event();
        location = new Location();
        event.setLocation(location);
    }

    static class TestCase {
        String description;
        int maxTickets;
        double popularityScore;
        LocalDateTime startTime;
        int locationEventCount;
        List<Integer> repoTicketSizes;
        boolean expected;

        TestCase(String description, int maxTickets, double popularityScore, LocalDateTime startTime,
                 int locationEventCount, List<Integer> repoTicketSizes, boolean expected) {
            this.description = description;
            this.maxTickets = maxTickets;
            this.popularityScore = popularityScore;
            this.startTime = startTime;
            this.locationEventCount = locationEventCount;
            this.repoTicketSizes = repoTicketSizes;
            this.expected = expected;
        }

        @Override
        public String toString() {
            return description;//for test case name to be set as description
        }
    }

    static Stream<TestCase> provideGeneralEventCases() {
        return Stream.of(
                new TestCase("Big, good, at night, not overused", 120, 4.5,
                        LocalDateTime.of(2025, 1, 1, 23, 0), 2, List.of(60, 90), true),

                new TestCase("Small, bad, night, overused", 60, 2.5,
                        LocalDateTime.of(2025, 1, 1, 22, 30), 4, List.of(100, 120), false),

                new TestCase("Big, bad, night, overused", 200, 2.0,
                        LocalDateTime.of(2025, 1, 1, 22, 0), 3, List.of(100, 120), false),

                new TestCase("Not big, good, before night, overused", 90, 4.2,
                        LocalDateTime.of(2025, 1, 1, 21, 0), 4, List.of(80, 100), false),

                new TestCase("Big, good, at night, overused", 200, 4.0,
                        LocalDateTime.of(2025, 1, 1, 23, 45), 3, List.of(100, 150), true),
                new TestCase("Exactly at 22:00, big and good", 200, 4.0,
                        LocalDateTime.of(2025, 1, 1, 22, 0), 1, List.of(100, 150), true),

                new TestCase("Exactly at 21:59, big and good", 200, 4.0,
                        LocalDateTime.of(2025, 1, 1, 21, 59), 1, List.of(100, 150), false),

                new TestCase("Popularity just below 3.0 (bad)", 150, 2.99,
                        LocalDateTime.of(2025, 1, 1, 22, 15), 3, List.of(100, 120), false),

                new TestCase("Exactly 2 events (not overused)", 200, 4.0,
                        LocalDateTime.of(2025, 1, 1, 22, 30), 2, List.of(100, 120), true),

                new TestCase("All false: not big, not overused, bad, not night", 50, 1.5,
                        LocalDateTime.of(2025, 1, 1, 20, 0), 1, List.of(100, 120), false)
        );
    }

    @ParameterizedTest()
    @MethodSource("provideGeneralEventCases")
    @DisplayName("Parameterized tests for combinations of values for return predicate")
    void testIsEventForAdultsOnly(TestCase testCase) {
        when(eventRepository.findAll()).thenReturn(
                testCase.repoTicketSizes.stream().map(this::createEvent).toList()
        );

        event.setMaxTickets(testCase.maxTickets);
        event.setPopularityScore(testCase.popularityScore);
        event.setStartTime(testCase.startTime);
        location.setEvents(Stream.generate(Event::new).limit(testCase.locationEventCount).toList());

        boolean result = eventService.isEventForAdultsOnly(event);
        assertEquals(testCase.expected, result, "Failed: " + testCase.description);
    }

    private Event createEvent(int maxTickets) {
        Event e = new Event();
        e.setMaxTickets(maxTickets);
        return e;
    }
}
