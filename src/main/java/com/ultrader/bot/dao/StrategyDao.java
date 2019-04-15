package com.ultrader.bot.dao;

import com.ultrader.bot.model.Strategy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

/**
 * Strategy Repository
 * @author ytx1991
 */
@RepositoryRestResource
@Transactional
public interface StrategyDao extends CrudRepository<Strategy, Long> {
}
