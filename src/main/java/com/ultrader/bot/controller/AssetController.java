package com.ultrader.bot.controller;

import com.ultrader.bot.dao.AssetListDao;
import com.ultrader.bot.model.AssetList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/asset")
@RestController("AssetController")
public class AssetController {
    private static Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private AssetListDao assetListDao;

    @RequestMapping(method = RequestMethod.GET, value = "/getAssetList")
    @ResponseBody
    public AssetList getPortfolio(@RequestParam String name) {
        try {
            Optional<AssetList> assetList = assetListDao.findById(name);
            if(assetList.isPresent()) {
                return assetList.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Get asset list failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setAssetList")
    @ResponseBody
    public AssetList setPortfolio(@RequestBody AssetList assetList) {
        try {
            return assetListDao.save(assetList);
        } catch (Exception e) {
            LOGGER.error("Get asset list failed.", e);
            return null;
        }
    }
}
