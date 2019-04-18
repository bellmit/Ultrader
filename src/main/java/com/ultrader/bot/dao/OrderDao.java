package com.ultrader.bot.dao;

import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Rule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

/**
 * Order Repository
 * @author ytx1991
 */
@RepositoryRestResource
@Transactional
public interface OrderDao extends CrudRepository<Order, String> {
}
