package vttp.batch5.groupb.day28_workshop.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.batch5.groupb.day28_workshop.repositories.GamesRepository;

@Service
public class GamesService {
    
    @Autowired
    private GamesRepository gamesRepo;

    public Optional<JsonObject> getGameWithReviews(int gid) {
        Optional<JsonObject> opt = gamesRepo.getGameWithReviews(gid);

        if (opt.isEmpty()) {
            return Optional.empty();
        }

        JsonObject jsonObj = opt.get();

        // get timestamp
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toEpochSecond(ZoneOffset.UTC);

        // Because it is immutable
        JsonObjectBuilder builder = Json.createObjectBuilder(jsonObj);
        builder.add("timestamp", timestamp);

        JsonObject newJsonObj = builder.build();

        return Optional.of(newJsonObj);
    }

    public List<JsonObject> getGamesWithHighestRating() {
        return gamesRepo.getGamesWithHighestRating();
    }
}
