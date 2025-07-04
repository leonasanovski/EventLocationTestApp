package mk.finki.ukim.mk.lab.isValidLocation.LC;
import static org.junit.jupiter.api.Assertions.*;

import mk.finki.ukim.mk.lab.model.Location;
import org.junit.jupiter.api.Test;

public class LocationValidationTestRACC {
    @Test
    void testRow29_nameBlank_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("   ");
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Valid long description");
        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow30_addressBlank_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("   ");
        loc.setCapacity("100");
        loc.setDescription("Valid long description");
        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow31_invalidCapacity_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("ABC");
        loc.setDescription("Valid long description");
        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow25_allValid_shouldReturnTrue() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Valid long description");
        assertTrue(loc.isValidLocation());
    }

    @Test
    void testRow9_allFalse_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("ABC");
        loc.setDescription("short");
        assertFalse(loc.isValidLocation());
    }
}
