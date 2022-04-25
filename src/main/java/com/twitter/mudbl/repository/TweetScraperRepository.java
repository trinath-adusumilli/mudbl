package com.twitter.mudbl.repository;

import com.twitter.mudbl.model.TweetScraper;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.function.Function;

@Repository
public interface TweetScraperRepository extends MongoRepository<TweetScraper, String> {

    @Override
    <S extends TweetScraper, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
