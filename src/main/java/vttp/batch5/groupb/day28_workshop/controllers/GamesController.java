package vttp.batch5.groupb.day28_workshop.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.batch5.groupb.day28_workshop.services.GamesService;

@RestController
@RequestMapping
public class GamesController {

    @Autowired
    private GamesService gamesSvc;
    
    @GetMapping(path="/game/{game_id}/reviews", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameWithReviews(
        @PathVariable(name="game_id") int gid
    ) {
        Optional<JsonObject> opt = gamesSvc.getGameWithReviews(gid);

        if (opt.isEmpty()) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("message", "Game id does not exist");
            JsonObject resp = builder.build();

            return ResponseEntity.status(404).body(resp.toString());
        }

        JsonObject resp = opt.get();

        return ResponseEntity.ok().body(resp.toString());
    }
}
