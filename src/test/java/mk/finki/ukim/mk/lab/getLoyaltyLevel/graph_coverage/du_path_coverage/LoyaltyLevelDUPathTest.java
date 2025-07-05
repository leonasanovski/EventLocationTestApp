package mk.finki.ukim.mk.lab.getLoyaltyLevel.graph_coverage.du_path_coverage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils.getLoyaltyLevel;
import static org.junit.jupiter.api.Assertions.*;

public class LoyaltyLevelDUPathTest {

    //1_1. du Path [1,2]
    @Test
    @DisplayName("du Path [1,2] (1)")
    void testDUPath1_1() {
        assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(null));
    }

    //1_2. du Path [1,2]
    @Test
    @DisplayName("du Path [1,2] (2)")
    void testDUPath1_2() {
        assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(-5));
    }

    //2. du Path [1,2,4]
    @DisplayName("du Path [1,2,4]")
    @Test
    void testDUPath2() {
        assertEquals("GOLD", getLoyaltyLevel(60));
    }

    //3. du Path [1,2,4,6]
    @Test
    @DisplayName("du Path [1,2,4,6]")
    void testDUPath3() {
        assertEquals("SILVER", getLoyaltyLevel(35));
    }

    //4. du Path [1,2,4,6,8]
    @Test
    @DisplayName("du Path [1,2,4,6,8]")
    void testDUPath4() {
        assertEquals("BRONZE", getLoyaltyLevel(20));
    }
}
