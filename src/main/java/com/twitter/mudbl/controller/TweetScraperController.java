package com.twitter.mudbl.controller;

import com.twitter.mudbl.model.TwitterWeeklyAverageData;
import com.twitter.mudbl.model.TwitterData;
import com.twitter.mudbl.service.TweetScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TweetScraperController {

    @Autowired
    TweetScraperService tweetScraperService;

    @GetMapping("/count")
    public long getCount() {
        return tweetScraperService.getCountOfObjects();
    }

    @GetMapping("/source")
    public List<TwitterData> getSource(@RequestParam String key) {
        return tweetScraperService.getSourceDetailsCount(key);
    }

    @GetMapping("/dailyPositiveNumber")
    public List<TwitterWeeklyAverageData> getDailyPositive(@RequestParam(required = false, value = "year") Integer year) {
        return tweetScraperService.getDailyPositiveNumber(year);
    }
}
