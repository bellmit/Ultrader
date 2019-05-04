package com.ultrader.bot.controller;

import com.ultrader.bot.model.StrategyMetadata;
import com.ultrader.bot.util.IndicatorType;
import com.ultrader.bot.util.RuleType;
import com.ultrader.bot.util.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata Controller
 * @author ytx1991
 */
@RequestMapping("/api/metadata")
@RestController
public class MetadataController {
    private final static Logger LOGGER = LoggerFactory.getLogger(MetadataController.class);


    @RequestMapping(method = RequestMethod.GET, value = "/getUserType")
    @ResponseBody
    public UserType[] getUserType() {
        return UserType.values();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getRuleType")
    @ResponseBody
    public RuleType[] getRuleType() {
        return RuleType.values();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getIndicatorType")
    @ResponseBody
    public IndicatorType[] getIndicatorType() {
        return IndicatorType.values();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCategoryIndicator")
    @ResponseBody
    public Map<String, List<String>> getIndicatorCategory() {
        Map<String, List<String>> map = new HashMap<>();
        for(Map.Entry<String, String> entry : IndicatorType.parentClassMap.entrySet()) {
            if(!map.containsKey(entry.getValue())) {
                map.put(entry.getValue(), new ArrayList<>());
            }
            map.get(entry.getValue()).add(entry.getKey());
        }
        return map;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStrategyMetadata")
    @ResponseBody
    public StrategyMetadata getStrategyMetadata() {
        return new StrategyMetadata(getRuleType(), getIndicatorType(), getIndicatorCategory());
    }

}
