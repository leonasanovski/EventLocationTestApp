package mk.finki.ukim.mk.lab.getLoyaltyLevel.basic_unit_testing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils.getLoyaltyLevel;


public class GetLoyaltyLevelTests {

    static Stream<Arguments> loyaltyData() {
        return Stream.of(
                Arguments.of(0, "REGULAR"),
                Arguments.of(10, "BRONZE"),
                Arguments.of(30, "SILVER"),
                Arguments.of(50, "GOLD")
        );
    }

    @ParameterizedTest
    @MethodSource("loyaltyData")
    void testLoyaltyLevels_MethodSource(int bookingCount, String expected) {
        Assertions.assertEquals(expected, getLoyaltyLevel(bookingCount));
    }

    @ParameterizedTest
    @CsvSource({
            "0,REGULAR",
            "5,REGULAR",
            "9,REGULAR",
            "10,BRONZE",
            "15,BRONZE",
            "29,BRONZE",
            "30,SILVER",
            "49,SILVER",
            "50,GOLD",
            "100,GOLD"
    })
    void testLoyaltyLevels_CsvSource(int bookingCount, String expectedLevel) {
        Assertions.assertEquals(expectedLevel, getLoyaltyLevel(bookingCount));
    }

    @Test
    void testRegularLevel() {
        Assertions.assertEquals("REGULAR", getLoyaltyLevel(0));
        Assertions.assertEquals("REGULAR", getLoyaltyLevel(5));
        Assertions.assertEquals("REGULAR", getLoyaltyLevel(9));
    }

    @Test
    void testBronzeLevel() {
        Assertions.assertEquals("BRONZE", getLoyaltyLevel(10));
        Assertions.assertEquals("BRONZE", getLoyaltyLevel(20));
        Assertions.assertEquals("BRONZE", getLoyaltyLevel(29));
    }

    @Test
    void testSilverLevel() {
        Assertions.assertEquals("SILVER", getLoyaltyLevel(30));
        Assertions.assertEquals("SILVER", getLoyaltyLevel(40));
        Assertions.assertEquals("SILVER", getLoyaltyLevel(49));
    }

    @Test
    void testGoldLevel() {
        Assertions.assertEquals("GOLD", getLoyaltyLevel(50));
        Assertions.assertEquals("GOLD", getLoyaltyLevel(75));
        Assertions.assertEquals("GOLD", getLoyaltyLevel(100));
    }

    @Test
    void testThrowsExceptionForNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(null));
    }

    @Test
    void testThrowsExceptionForNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getLoyaltyLevel(-10));
    }
}
