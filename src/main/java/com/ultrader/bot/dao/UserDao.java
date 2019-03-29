package com.ultrader.bot.dao;

import com.ultrader.bot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

@RepositoryRestResource
@Transactional
public interface UserDao extends CrudRepository<User, Long> {
}
