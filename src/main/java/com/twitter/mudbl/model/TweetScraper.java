package com.twitter.mudbl.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tweetday")
@Getter
@Setter
@Data
public class TweetScraper {
    @Id
    String id;
    boolean isReply;
    boolean isRetweet;
    String text;
    String source;
}
