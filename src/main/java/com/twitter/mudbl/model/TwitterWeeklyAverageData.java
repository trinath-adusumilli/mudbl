package com.twitter.mudbl.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tweetday")
@Getter
@Setter
@Data
public class TwitterWeeklyAverageData {

    private String weekNumber;

    private String average;

    private String year;

    private String startDate;

    private String endDate;

}
