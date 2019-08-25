package com.ultrader.bot.dao;

import com.ultrader.bot.model.Notification;
import com.ultrader.bot.model.Rule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Notification Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface NotificationDao extends CrudRepository<Notification, String> {
    @Query(value = "SELECT * FROM notifications order by date desc limit ?1",
            nativeQuery = true)
    public List<Notification> getLatestNotification(int num);
}
