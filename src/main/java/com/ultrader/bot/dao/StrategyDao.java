package com.ultrader.bot.dao;

import com.ultrader.bot.model.Strategy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Strategy Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface StrategyDao extends CrudRepository<Strategy, Long> {
}
