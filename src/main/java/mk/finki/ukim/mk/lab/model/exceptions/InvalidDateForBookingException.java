package mk.finki.ukim.mk.lab.model.exceptions;

public class InvalidDateForBookingException extends RuntimeException {
    public InvalidDateForBookingException(String message) {
        super(message);
    }
}
