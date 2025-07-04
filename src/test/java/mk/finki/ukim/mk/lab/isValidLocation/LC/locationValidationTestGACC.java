package mk.finki.ukim.mk.lab.isValidLocation.LC;

import static org.junit.jupiter.api.Assertions.*;

import mk.finki.ukim.mk.lab.model.Location;
import org.junit.jupiter.api.Test;

public class locationValidationTestGACC {

    @Test
    void testRow13_nameBlank_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("   ");
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Large hall for meetings");

        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow14_addressBlank_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("   ");
        loc.setCapacity("100");
        loc.setDescription("Large hall for meetings");

        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow15_invalidCapacity_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("ABC");
        loc.setDescription("Large hall for meetings");

        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow21_descriptionTooShort_shouldReturnFalse() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Short");

        assertFalse(loc.isValidLocation());
    }

    @Test
    void testRow25_allValid_shouldReturnTrue() {
        Location loc = new Location();
        loc.setName("Office");
        loc.setAddress("Main Street");
        loc.setCapacity("100");
        loc.setDescription("Large hall for meetings");

        assertTrue(loc.isValidLocation());
    }
}