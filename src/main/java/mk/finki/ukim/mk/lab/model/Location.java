package mk.finki.ukim.mk.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "location")
public class Location {
    //  this will make Long id as the id key for the database and will make it automatically as a number and
    //  every next will be before_id++
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String capacity;
    private String description;
    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    //with this functionality I am allowing the base to delete every event that is connected with this location
    private List<Event> events;

    public Location(String name, String address, String capacity, String description) {
//        this.id = (long) (Math.random() * 1000);
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
//        this.events = events;
    }
}
