package com.ultrader.bot.dao;

import com.ultrader.bot.model.Chart;
import com.ultrader.bot.model.Position;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Position Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface PositionDao extends CrudRepository<Position, String> {
}
