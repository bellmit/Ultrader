package com.ultrader.bot.dao;

import com.ultrader.bot.model.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Setting Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface SettingDao extends CrudRepository<Setting, String> {
    List<Setting> findByName(String name);
}
