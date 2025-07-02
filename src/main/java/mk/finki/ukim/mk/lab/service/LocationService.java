package mk.finki.ukim.mk.lab.service;

import mk.finki.ukim.mk.lab.model.Location;

import java.util.List;
import java.util.Optional;


public interface LocationService {
    public List<Location> findAll();
    void delete(Long id);
    void save_location(String name, String address, String capacity, String description);
    public Optional<Location> findById(Long id);
}
