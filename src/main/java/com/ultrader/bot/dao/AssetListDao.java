package com.ultrader.bot.dao;

import com.ultrader.bot.model.AssetList;
import com.ultrader.bot.model.Position;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Asset List Repository
 * @author ytx1991
 */
@Repository
@Transactional
public interface AssetListDao extends CrudRepository<AssetList, String> {
}
