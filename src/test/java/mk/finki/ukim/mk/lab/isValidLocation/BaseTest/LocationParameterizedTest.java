package mk.finki.ukim.mk.lab.isValidLocation.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

import mk.finki.ukim.mk.lab.model.Location;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.stream.Stream;

public class LocationParameterizedTest {

    //  Using @ValueSource:
    @ParameterizedTest
    @ValueSource(strings = { " ", "", "   " })
    void testInvalidNamesWithValueSource(String name) {
        Location loc = new Location();
        loc.setName(name);
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Valid description here.");
        assertFalse(loc.isValidLocation());
    }

    //  Using @CsvSource:s
    @ParameterizedTest
    @CsvSource({
            "Office,Main St,100,This is a valid long description.,true",
            "'',Main St,100,This is a valid long description.,false",
            "Office,'',100,This is a valid long description.,false",
            "Office,Main St,ABC,This is a valid long description.,false",
            "Office,Main St,100,Short,false"
    })
    void testVariousCombinationsWithCsvSource(String name, String address, String capacity, String description, boolean expected) {
        Location loc = new Location();
        loc.setName(name);
        loc.setAddress(address);
        loc.setCapacity(capacity);
        loc.setDescription(description);
        assertEquals(expected, loc.isValidLocation());
    }

    //  Using @MethodSource:
    static Stream<Arguments> provideLocations() {
        return Stream.of(
                Arguments.of("Conference", "123 St", "100", "Valid description here", true),
                Arguments.of("   ", "123 St", "100", "Valid description here", false),
                Arguments.of("Office", "   ", "100", "Valid description here", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLocations")
    void testWithMethodSource(String name, String address, String capacity, String description, boolean expected) {
        Location loc = new Location();
        loc.setName(name);
        loc.setAddress(address);
        loc.setCapacity(capacity);
        loc.setDescription(description);
        assertEquals(expected, loc.isValidLocation());
    }

    //  Using @EnumSource:
    enum CapacityValues {
        VALID("100"), INVALID("abc");

        public final String value;

        CapacityValues(String value) {
            this.value = value;
        }
    }

    @ParameterizedTest
    @EnumSource(CapacityValues.class)
    void testCapacityWithEnumSource(CapacityValues cap) {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main St");
        loc.setCapacity(cap.value);
        loc.setDescription("Valid description here.");
        boolean expected = cap == CapacityValues.VALID;
        assertEquals(expected, loc.isValidLocation());
    }
}