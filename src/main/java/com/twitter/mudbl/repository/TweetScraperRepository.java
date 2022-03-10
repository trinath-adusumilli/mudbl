package com.twitter.mudbl.repository;

import com.twitter.mudbl.model.TweetScraper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetScraperRepository extends MongoRepository<TweetScraper, String> {

}
