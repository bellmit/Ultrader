package com.ultrader.bot.dao;

import com.ultrader.bot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * User Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
