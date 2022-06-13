package com.twitter.mudbl.service;

import com.twitter.mudbl.model.TweetScraper;
import com.twitter.mudbl.model.TwitterData;
import com.twitter.mudbl.model.TwitterWeeklyAverageData;
import com.twitter.mudbl.repository.TweetScraperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.DateOperators.Week.weekOf;
import static org.springframework.data.mongodb.core.aggregation.DateOperators.Year.yearOf;

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

    public List<TwitterWeeklyAverageData> getDailyPositiveNumber(String year) {

        AggregationResults<TwitterWeeklyAverageData> groupResults = null;
        Aggregation aggregation = null;

        if (year == null) {

//            This is a aggregate expression for daily tweet count
//            aggregation = newAggregation(
//                    match(Criteria.where("model_label_result").is(true)),
//                    project("model_label_result")
//                            .and(StringOperators.valueOf("created_at").substring(0, 10))
//                            .as("dateCount"),
//                    group("$dateCount").count().as("total"),
//                    project("total").and("date").previousOperation(),
//                    sort(Sort.Direction.ASC, "date"));

            //This is an aggregation for weekly tweet average
            aggregation = newAggregation(
                    match(Criteria.where("model_label_result").is(true)),
                    project()
                            .and(weekOf(ConvertOperators.ToDate.toDate(DateOperators.DateFromString.fromStringOf("created_at"))))
                            .as("week")
                            .and(yearOf(ConvertOperators.ToDate.toDate(DateOperators.DateFromString.fromStringOf("created_at")))).as("year"),
                    group("week", "year").count().as("total"),
                    project()
                            .and("total").divide(7).as("average")
                            .and("year").as("year")
                            .and("week").as("weekNumber"),
                    sort(Sort.Direction.ASC, "year", "weekNumber"));
        } else {
            String currentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now().minusYears(Integer.parseInt(year)));

            //This is an aggregation for daily tweet count
//            aggregation = newAggregation(
//                    match(Criteria.where("model_label_result").is(true)),
//                    project("model_label_result")
//                            .and(StringOperators.valueOf("created_at").substring(0, 10))
//                            .as("dateCount"),
//                    group("$dateCount").count().as("total"),
//                    project("total").and("date").previousOperation(),
//                    match(Criteria.where("date").gte(currentDate)),
//                    sort(Sort.Direction.ASC, "date"));

            //This is an aggregation for weekly tweet average
            aggregation = newAggregation(
                    match(Criteria.where("model_label_result").is(true)),
                    match(Criteria.where("created_at").gte(currentDate)),
                    project()
                            .and(weekOf(ConvertOperators.ToDate.toDate(DateOperators.DateFromString.fromStringOf("created_at"))))
                            .as("week")
                            .and(yearOf(ConvertOperators.ToDate.toDate(DateOperators.DateFromString.fromStringOf("created_at")))).as("year"),
                    group("week", "year").count().as("total"),
                    project()
                            .and("total").divide(7).as("average")
                            .and("year").as("year")
                            .and("week").as("weekNumber"),
                    sort(Sort.Direction.ASC, "year", "weekNumber"));
        }


        groupResults = mongoTemplate.aggregate(aggregation, TweetScraper.class, TwitterWeeklyAverageData.class);

        for (TwitterWeeklyAverageData twitterWeeklyAverageData : groupResults.getMappedResults()) {
            twitterWeeklyAverageData.setStartDate(getFirstDayOfWeek(Integer.parseInt(twitterWeeklyAverageData.getWeekNumber()), Integer.parseInt(twitterWeeklyAverageData.getYear())));
            twitterWeeklyAverageData.setEndDate(getLastDayOfWeek(Integer.parseInt(twitterWeeklyAverageData.getWeekNumber()), Integer.parseInt(twitterWeeklyAverageData.getYear())));
        }

        return groupResults.getMappedResults();
    }

    static String getFirstDayOfWeek(int weekNumber, int year) {

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(cal.getTime());
    }

    static String getLastDayOfWeek(int weekNumber, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        cal.add(Calendar.DATE, 7);//Add 6 days to get Sunday of next week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(cal.getTime());
    }

    public String getRelatedWords(String word) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("word", word);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://app.itongue.cn:8085/word", request, String.class);

        return response.getBody();
    }
}
