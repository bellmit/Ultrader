package com.ultrader.bot.controller;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.model.Rule;
import com.ultrader.bot.util.IndicatorType;
import com.ultrader.bot.util.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rule Controller
 * @author ytx1991
 */

@RequestMapping("/rule")
@RestController
public class RuleController {
    private static Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    @Autowired
    private RuleDao ruleDao;

    @RequestMapping(method = RequestMethod.POST, value = "/addRule")
    @ResponseBody
    public Rule saveRule(@RequestBody Rule rule) {
        try {
            Rule savedRule = ruleDao.save(rule);
            return savedRule;
        } catch (Exception e) {
            LOGGER.error("Save rule failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getRule")
    @ResponseBody
    public Rule getRule(@PathVariable Long id) {
        try {
            Optional<Rule> rule = ruleDao.findById(id);
            return rule.get();
        } catch (Exception e) {
            LOGGER.error("Get rule failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getRules")
    @ResponseBody
    public Iterable<Rule> getRules() {
        try {
            Iterable<Rule> rules = ruleDao.findAll();
            return rules;
        } catch (Exception e) {
            LOGGER.error("Get rules failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteRule")
    @ResponseBody
    public void deleteRule(@PathVariable Long id) {
        try {
            ruleDao.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("Delete rule failed.", e);
        }
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

    @RequestMapping(method = RequestMethod.GET, value = "/getIndicatorCategory")
    @ResponseBody
    public Map<String, String> getIndicatorCategory() {
        return IndicatorType.parentClassMap;
    }

}
