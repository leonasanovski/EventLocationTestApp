package mk.finki.ukim.mk.lab.service.implementation;

import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImplementation implements LocationService {
    public final LocationRepository locationRepository;

    public LocationServiceImplementation(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> findAll() {return locationRepository.findAll();}

    @Override
    public void delete(Long id) {
        this.locationRepository.deleteById(id);
    }

    @Override
    public void save_location(String name, String address, String capacity, String description) {
        Location location = new Location(name,address,capacity,description);
        this.locationRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

}
