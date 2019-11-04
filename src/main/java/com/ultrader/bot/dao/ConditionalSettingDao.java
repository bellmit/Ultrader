package com.ultrader.bot.dao;

import com.ultrader.bot.model.ConditionalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Conditional setting Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface ConditionalSettingDao extends CrudRepository<ConditionalSetting, Long> {
    List<ConditionalSetting> findByMarketTrend(String marketTrend);
}
