package com.ultrader.bot.controller;

import com.ultrader.bot.dao.AssetListDao;
import com.ultrader.bot.model.AssetList;
import com.ultrader.bot.model.alpaca.Asset;
import com.ultrader.bot.monitor.MarketDataMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAssetList/{id}")
    @ResponseBody
    public void deleteAssetList(@PathVariable String id) {
        try {
            assetListDao.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("Delete Asset List failed.", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllAssets")
    @ResponseBody
    public Iterable<Asset> getPortfolio() {
        try {
            List<Asset> assets = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : MarketDataMonitor.getInstance().getAvailableStock().entrySet()) {
                for (String symbol : entry.getValue()) {
                    assets.add(new Asset(symbol, "stock", entry.getKey(), symbol, "", true));
                }
            }
            return assets;
        } catch (Exception e) {
            LOGGER.error("Get asset list failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAssetLists")
    @ResponseBody
    public Iterable<AssetList> getAssetLists() {
        try {
            Iterable<AssetList> assetLists = assetListDao.findAll();
            return assetLists;
        } catch (Exception e) {
            LOGGER.error("Get asset lists failed.", e);
            return null;
        }
    }
}
