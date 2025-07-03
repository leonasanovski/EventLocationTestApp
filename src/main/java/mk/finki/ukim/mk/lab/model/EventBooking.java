package mk.finki.ukim.mk.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventBooking {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Event event;
    private String attendeeName;
    private String attendeeAddress;
    private Long numberOfTickets;
    private double totalPrice;

    public EventBooking(Event event, String attendeeName, String attendeeAddress, Long numberOfTickets, double totalPrice) {
        this.event = event;
        this.attendeeName = attendeeName;
        this.attendeeAddress = attendeeAddress;
        this.numberOfTickets = numberOfTickets;
        this.totalPrice = totalPrice;
    }
}
