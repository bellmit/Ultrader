package com.ultrader.bot.dao;

import com.ultrader.bot.model.Chart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Chart Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface ChartDao extends CrudRepository<Chart, Long> {
}
