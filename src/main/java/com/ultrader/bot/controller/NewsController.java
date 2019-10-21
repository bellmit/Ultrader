package com.ultrader.bot.controller;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * News Controller.
 * @author pantelepathy
 */

@RequestMapping("/api/news")
@RestController("NewsController")
public class NewsController {
    private static Logger LOGGER = LoggerFactory.getLogger(NewsController.class);

    private static final String NEWS_CLIENT_URI = "http://www.ultraderbot.com/api/news/getNews";

    private final RestTemplate client;

    @Autowired
    public NewsController(final RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        this.client = restTemplateBuilder.rootUri(NEWS_CLIENT_URI).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getNewsList")
    @ResponseBody
    public Object getNewsList(@RequestParam("num") Long num) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(NEWS_CLIENT_URI)
                .queryParam("num", num);
            HttpEntity<String> response = client.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("Get news failed.", e);
            return null;
        }
    }
}
