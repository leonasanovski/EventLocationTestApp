package mk.finki.ukim.mk.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidDateForBookingException;
import mk.finki.ukim.mk.lab.model.exceptions.InvalidRangeSetException;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double popularityScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double basePrice;
    private Integer maxTickets;
    @ManyToOne
    private Location location;

    public Event(String name, String description, double popularityScore, Location location,
                 LocalDateTime startTime, LocalDateTime endTime, double basePrice, Integer maxTickets) {
        this.name = name;
        this.description = description;
        this.popularityScore = popularityScore;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.maxTickets = maxTickets;
    }
    //constructor for the test for graph coverage
    public Event(double basePrice, int maxTickets, LocalDateTime start, LocalDateTime end){
        this.basePrice = basePrice;
        this.maxTickets = maxTickets;
        this.startTime = start;
        this.endTime = end;
    }

    public Event(String name, int i, LocalDateTime now) {
        this.name = name;
        this.startTime = now;

    }

    //LEON
    //This method is chosen for ISP (Base Coverage) testing
    public double calculatePrice(Integer numTickets, Integer ticketsBookedSoFar, LocalDateTime bookingTime) {
        if (numTickets == null || numTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets must be a positive integer.");
        }
        if (ticketsBookedSoFar == null || ticketsBookedSoFar < 0) {
            throw new IllegalArgumentException("Tickets booked so far must be zero or a positive integer.");
        }
        if (bookingTime == null) {
            throw new IllegalArgumentException("Booking time cannot be null.");
        }
        if (bookingTime.isAfter(this.endTime)) {
            throw new InvalidDateForBookingException("Booking time cannot be after the event has ended.");
        }
        if (!bookingTime.isBefore(this.startTime)) {
            throw new InvalidDateForBookingException("Booking must be made before the event starts.");
        }
        if (maxTickets < 0) {
            throw new InvalidRangeSetException("Max tickets must be a positive integer.");
        }
        if (maxTickets == 0) {
            throw new ArithmeticException("Dividing by zero!");
        }
        double price = basePrice;
        double demandRatio = (double) ticketsBookedSoFar / maxTickets;
        if (demandRatio > 0.75) {
            price *= 1.2;
        }
        if (bookingTime.isBefore(this.startTime.minusDays(30))) {
            price *= 0.9;
        }
        return price * numTickets;
    }

}
