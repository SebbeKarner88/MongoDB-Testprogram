package com.example.mongodbtestprogram.Service;

import com.example.mongodbtestprogram.Entities.RideEntity;
import com.example.mongodbtestprogram.Entities.UserEntity;
import com.example.mongodbtestprogram.Functions.Functions;
import com.example.mongodbtestprogram.Repositories.RideRepository;
import com.example.mongodbtestprogram.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    @Autowired
    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    public List<RideEntity> getAll() {
        return rideRepository.findAll();
    }

    public RideEntity addRide(RideEntity rideEntity) {

       Optional<UserEntity> user = userRepository.findByUsername(rideEntity.getUser().getUsername());

       if (user.isPresent()) {

           BigDecimal distanceRounded = new BigDecimal(
                   Functions.distance(                               // DISPLAYED IN KM
                           rideEntity.getStartLoc().getLatitude(),
                           rideEntity.getEndLoc().getLatitude(),
                           rideEntity.getStartLoc().getLongitude(),
                           rideEntity.getEndLoc().getLongitude())
           ).setScale(2, RoundingMode.HALF_UP);

           Duration d = Duration.between(rideEntity.getStartTime(), rideEntity.getEndTime());

           String durationDisplay = Functions.formatDuration(d);    // RETURNS A STRING WITH TIME

           Double avgSpeed = distanceRounded.doubleValue() / (((double) d.toSeconds() / 60) / 60); // Displayed in KMT

           RideEntity ride = new RideEntity(
                   UUID.randomUUID(),
                   rideEntity.getUser(),
                   rideEntity.getBike(),
                   rideEntity.getStartTime(),
                   rideEntity.getEndTime(),
                   rideEntity.getStartLoc(),
                   rideEntity.getEndLoc(),
                   distanceRounded.doubleValue(),
                   durationDisplay,
                   avgSpeed
           );

           rideRepository.save(ride);
           return ride;
       }
       return null;
    }

    public List<RideEntity> getAllByUser(String username) {

        List<RideEntity> rides = rideRepository.findRideEntitiesByUser_Username(username);

        if (rides.isEmpty())
            return null;

        return rides;
    }
}
