package com.ultrader.bot.dao;

import com.ultrader.bot.model.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Setting Repository
 * @author ytx1991
 */
@RepositoryRestResource
@Transactional
public interface SettingDao extends CrudRepository<Setting, String> {
    List<Setting> findByName(String name);
}
