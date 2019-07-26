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
    public List<Chart> getDataByName(LocalDateTime startDate, LocalDateTime endDate, String name);

    @Query(value = "SELECT * FROM CHARTS order by id desc limit 1",
            nativeQuery = true)
    public List<Chart> getLastId();

    @Query(value = "SELECT * FROM CHARTS where date < ?1 and serial_Name = ?2 order by date desc limit 1",
            nativeQuery = true)
    public List<Chart> getDataByDate(LocalDateTime date, String name);
}
