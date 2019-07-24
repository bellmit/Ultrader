package com.ultrader.bot.util;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Optional;

/**
 * Util of processing repository result
 * @author ytx1991
 */
public class RepositoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUtil.class);
    public static String getSetting(SettingDao dao, String settingName, String defaultValue) {
        Validate.notNull(dao, "setting dao is required");
        Optional<Setting> setting = dao.findById(settingName);
        return setting.isPresent() ? setting.get().getValue() : defaultValue;
    }

}
