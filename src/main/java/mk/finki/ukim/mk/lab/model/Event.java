package mk.finki.ukim.mk.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double popularityScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double basePrice;
    private int maxTickets;
    @ManyToOne
    private Location location;

    public Event(String name, String description, double popularityScore, Location location,
                 LocalDateTime startTime, LocalDateTime endTime, double basePrice, int maxTickets) {
        this.name = name;
        this.description = description;
        this.popularityScore = popularityScore;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.maxTickets = maxTickets;
    }

    public double calculatePrice(int numTickets, int ticketsBookedSoFar, LocalDateTime bookingTime) {
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
