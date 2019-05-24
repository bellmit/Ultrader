package com.ultrader.bot.dao;

import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Rule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Order Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface OrderDao extends CrudRepository<Order, String> {
    @Query(value = "SELECT * FROM TRADINGS WHERE close_date > ?1 and close_date < ?2 order by close_date desc",
            nativeQuery = true)
    public List<Order> findAllOrdersByDate(Date startDate, Date endDate);
}
