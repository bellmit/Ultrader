package com.ultrader.bot.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * Util for Database
 */
public class DatabaseUtil {
    public static void backup(JdbcTemplate jdbcTemplate) {
        int id = LocalDateTime.now().getDayOfWeek().getValue();
        jdbcTemplate.execute("BACKUP TO 'data/backup" + id + ".zip'");
    }
}
