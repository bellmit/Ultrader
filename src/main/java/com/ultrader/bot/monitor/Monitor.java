package com.ultrader.bot.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For long running task
 * @author ytx1991
 */
public abstract class Monitor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);
    //The frequency of running scanning (millisecond)
    private Long interval;

    Monitor(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        while(true) {
            try {
                scan();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                LOGGER.error("Monitor interrupted.",e);
                break;
            } catch (Exception e) {
                LOGGER.error("Failed on scan.", e);
            }
        }
    }

    abstract void  scan();

    public void setInterval(long interval) {
        this.interval = interval;
    }
    public long getInterval() {
        return this.interval;
    }
}
