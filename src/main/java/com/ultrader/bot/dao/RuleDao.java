package com.ultrader.bot.dao;

import com.ultrader.bot.model.Rule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

/**
 * Rule Repository
 * @author ytx1991
 */
@RepositoryRestResource
@Transactional
public interface RuleDao extends CrudRepository<Rule, Long> {
}
