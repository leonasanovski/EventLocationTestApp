package mk.finki.ukim.mk.lab.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCart {
    private String attendeeName;
    private String address;
    private List<EventBooking> items = new ArrayList<>();
    public double getTotalPrice() {
        return items.stream().mapToDouble(EventBooking::getTotalPrice).sum();
    }
}