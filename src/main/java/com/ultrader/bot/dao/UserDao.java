package com.ultrader.bot.dao;

import com.ultrader.bot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

/**
 * User Repository
 * @author ytx1991
 */
@RepositoryRestResource
@Transactional
public interface UserDao extends CrudRepository<User, Long> {
    List<User> findByUsername(String username);
}
