package com.ultrader.bot.dao;

import com.ultrader.bot.model.HistoryMarketData;
import com.ultrader.bot.model.Rule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * HistoryMarketData Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface HistoryMarketDataDao extends CrudRepository<HistoryMarketData, Long> {
}
