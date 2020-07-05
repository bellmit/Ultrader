package com.ultrader.bot.dao;

import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Rule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Order Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface OrderDao extends CrudRepository<Order, String> {
    @Query(value = "SELECT * FROM TRADINGS WHERE status='filled' and close_date > ?1 and close_date < ?2 order by close_date desc",
            nativeQuery = true)
    public List<Order> findAllOrdersByDate(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value = "SELECT * FROM TRADINGS WHERE status in ('filled', 'accepted') and symbol = ?1 and close_date <= ?2 order by close_date desc limit 2",
            nativeQuery = true)
    public List<Order> findLastTradeBySymbol(String symbol, LocalDateTime sellDate);
}
