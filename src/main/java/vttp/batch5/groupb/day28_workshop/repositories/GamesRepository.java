package vttp.batch5.groupb.day28_workshop.repositories;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Repository
public class GamesRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * db.games.aggregate([
            {
                $match: {gid : NumberInt(5)}
            },
            {
                $lookup : {
                    from: 'comments',
                    foreignField: 'gid',
                    localField: 'gid',
                    as: 'reviews',
                    pipeline : [
                        {$sort : {rating : -1}}
                    ]
                }
            },
            {
                $project: {
                    _id:0,
                    gid:1,
                    name:1,
                    year:1,
                    rank:1,
                    users_rated:1,
                    url:1,
                    image:1,
                    'reviews.user': 1,
                    'reviews.rating':1,
                    'reviews.c_text':1
                }
            },
        ])
     * 
     */
    public Optional<JsonObject> getGameWithReviews(int gid) {
        MatchOperation matchGid = Aggregation.match(Criteria.where("gid").is(gid));

        SortOperation sortByRating = Aggregation.sort(Direction.DESC, "rating");
        
        LookupOperation lookupReviews = LookupOperation.newLookup()
            .from("comments").localField("gid").foreignField("gid")
            .pipeline(sortByRating)
            .as("reviews");

        Aggregation pipeline = Aggregation.newAggregation(matchGid, lookupReviews);

        AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, "games", Document.class);

        List<Document> resultsList = results.getMappedResults();

        if (resultsList.isEmpty()) {
            return Optional.empty();
        }

        Document d = resultsList.get(0);

        // Convert the Document to JsonObject using Jakarta JSON
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("gid", d.getInteger("gid"));
        builder.add("name", d.getString("name"));
        builder.add("year", d.getInteger("year"));
        builder.add("users_rated", d.getInteger("users_rated"));
        builder.add("url", d.getString("url")) ;
        builder.add("image", d.getString("image"));

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        // Handle the "reviews" field which is an array of documents
        List<Document> reviews = (List<Document>) d.get("reviews");

        if (reviews != null) {
            for (Document review : reviews) {
                JsonObjectBuilder reviewBuilder = Json.createObjectBuilder();
                reviewBuilder.add("user", review.getString("user"));
                reviewBuilder.add("rating", review.getInteger("rating"));
                reviewBuilder.add("c_text", review.getString("c_text"));
                arrayBuilder.add(reviewBuilder.build());
            }
        }

        builder.add("reviews", arrayBuilder.build());

        return Optional.of(builder.build());
    }
}
