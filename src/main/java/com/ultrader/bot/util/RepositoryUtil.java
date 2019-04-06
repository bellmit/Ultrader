package com.ultrader.bot.util;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import org.apache.commons.lang.Validate;
import org.springframework.util.DigestUtils;

import java.util.Optional;

/**
 * Util of processing repository result
 * @author ytx1991
 */
public class RepositoryUtil {
    public static String getSetting(SettingDao dao, String settingName, String defaultValue) {
        Validate.notNull(dao, "setting dao is required");
        return dao.findById(settingName).map(Setting::getValue).orElse(defaultValue);
    }

}
