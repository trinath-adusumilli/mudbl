package com.twitter.mudbl.service;

import com.twitter.mudbl.model.TweetScraper;
import com.twitter.mudbl.model.TwitterData;
import com.twitter.mudbl.repository.TweetScraperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class TweetScraperService {

    @Autowired
    TweetScraperRepository tweetScraperRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public long getCountOfObjects() {
        return tweetScraperRepository.count();
    }

    public List<TwitterData> getSourceDetailsCount(String key) {

        Aggregation agg = newAggregation(
                group(key).count().as("total"),
                project("total").and("key").previousOperation(),
                sort(Sort.Direction.DESC, "total"));

        AggregationResults<TwitterData> groupResults
                = mongoTemplate.aggregate(agg, TweetScraper.class, TwitterData.class);

        List<TwitterData> result = groupResults.getMappedResults();

        return result;
    }
}
