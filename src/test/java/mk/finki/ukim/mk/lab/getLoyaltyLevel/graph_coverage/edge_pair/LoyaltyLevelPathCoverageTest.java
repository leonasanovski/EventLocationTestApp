package mk.finki.ukim.mk.lab.getLoyaltyLevel.graph_coverage.edge_pair;

import static mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils.getLoyaltyLevel;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LoyaltyLevelPathCoverageTest {

    @Test
    void testThrowsExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(null));
    }

    @Test
    void testReturnsGold() {
        assertEquals("GOLD", getLoyaltyLevel(60));
    }

    @Test
    void testReturnsSilver() {
        assertEquals("SILVER", getLoyaltyLevel(35));
    }

    @Test
    void testReturnsRegular() {
        assertEquals("REGULAR", getLoyaltyLevel(5));
    }

    @Test
    void testReturnsBronze() {
        assertEquals("BRONZE", getLoyaltyLevel(20));
    }
}
