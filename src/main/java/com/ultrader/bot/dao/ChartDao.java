package com.ultrader.bot.dao;

import com.ultrader.bot.model.Chart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Chart Repository
 * @author ytx1991
 */
@Repository("ChartDao")
@Transactional
public interface ChartDao extends CrudRepository<Chart, Long> {
    @Query(value = "SELECT * FROM CHARTS WHERE date > ?1 and date < ?2 and serial_Name = ?3 order by date asc",
            nativeQuery = true)
    public List<Chart> getPortfolioByDate(LocalDateTime startDate, LocalDateTime endDate, String name);
}
