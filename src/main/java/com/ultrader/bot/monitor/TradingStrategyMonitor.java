package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;

import com.ultrader.bot.service.alpaca.AlpacaPaperTradingService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Trading strategy monitor
 * @author ytx1991
 */
public class TradingStrategyMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingStrategyMonitor.class);
    private static TradingStrategyMonitor singleton_instance = null;
    public static Map<String, Strategy> strategies = new HashMap<>();
    public static Object lock = new Object();
    private final SettingDao settingDao;
    private final StrategyDao strategyDao;
    private final RuleDao ruleDao;
    private final AlpacaTradingService alpacaTradingService;
    private final AlpacaPaperTradingService alpacaPaperTradingService;

    TradingStrategyMonitor(long interval, final AlpacaTradingService alpacaTradingService, final AlpacaPaperTradingService alpacaPaperTradingService, final SettingDao settingDao, final StrategyDao strategyDao, final RuleDao ruleDao) {
        super(interval);
        Validate.notNull(alpacaTradingService, "alpacaTradingService is required");
        Validate.notNull(alpacaPaperTradingService, "alpacaPaperTradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");

        this.settingDao = settingDao;
        this.strategyDao = strategyDao;
        this.ruleDao = ruleDao;
        this.alpacaTradingService = alpacaTradingService;
        this.alpacaPaperTradingService = alpacaPaperTradingService;
    }

    public static void init(long interval, AlpacaTradingService alpacaTradingService, final AlpacaPaperTradingService alpacaPaperTradingService, SettingDao settingDao, StrategyDao strategyDao, RuleDao ruleDao) {
        singleton_instance = new TradingStrategyMonitor(interval, alpacaTradingService, alpacaPaperTradingService, settingDao, strategyDao, ruleDao);
    }

    public static TradingStrategyMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    @Override
    void scan() {
        try {

            if(!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            if(!Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.AUTO_TRADING_ENABLE.getName(), "true"))){
                LOGGER.info("Auto trading disabled");
                return;
            }
            LOGGER.info("Execute trading strategy.");
            synchronized (lock) {
                TradingUtil.updateStrategies(strategyDao, ruleDao, settingDao);
                for(Map.Entry<String, Strategy> entry : strategies.entrySet()) {
                    if(MarketDataMonitor.timeSeriesMap.containsKey(entry.getKey()) && MarketDataMonitor.isMarketOpen()) {
                        if(entry.getValue().shouldEnter(MarketDataMonitor.timeSeriesMap.get(entry.getKey()).getEndIndex()) && true) {
                            //buy strategy satisfy & no position

                            LOGGER.info(String.format("%s buy strategy satisfied.", entry.getKey()));
                        } else if (entry.getValue().shouldExit(MarketDataMonitor.timeSeriesMap.get(entry.getKey()).getEndIndex()) && true) {
                            //sell strategy satisfy & has position
                            LOGGER.info(String.format("%s sell strategy satisfied.", entry.getKey()));
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute trading strategy.", e);
        }
    }


}
