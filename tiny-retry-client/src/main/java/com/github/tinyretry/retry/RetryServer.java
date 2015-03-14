package com.github.tinyretry.retry;

import static com.github.tinyretry.retry.constants.Constants.RETRY_SYS_APP_CODE;
import static com.github.tinyretry.retry.constants.Constants.RETRY_SYS_COUNT;
import static com.github.tinyretry.retry.constants.Constants.RETRY_SYS_EXECUTE_TIMEOUT;
import static com.github.tinyretry.retry.constants.Constants.RETRY_SYS_PERIOD_MILLISECOND;
import static com.github.tinyretry.retry.constants.RetryServerStatus.BORN;
import static com.github.tinyretry.retry.constants.RetryServerStatus.DESTROYED;
import static com.github.tinyretry.retry.constants.RetryServerStatus.INITED;
import static com.github.tinyretry.retry.constants.RetryServerStatus.RUNNING;
import static com.github.tinyretry.retry.constants.RetryServerStatus.STOPED;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.commons.model.LifeCycle;
import com.github.tinyretry.retry.conf.Configureable;
import com.github.tinyretry.retry.conf.Configuretion;
import com.github.tinyretry.retry.conf.DefaultConfiguration;
import com.github.tinyretry.retry.constants.Constants;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.scanner.RetryScanner;
import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.ext.McJobDefinition;

/**
 * <pre>
 * desc: 重试框架服务
 * 
 * created: 2012-9-3 下午04:42:17
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class RetryServer implements LifeCycle, Configureable, ApplicationContextAware {

    private static final Logger logger        = LoggerFactory.getLogger(RetryServer.class);
    private ApplicationContext  applicationContext;
    private volatile int        status        = BORN.getStatus();
    private List<RetryScanner>  scanners;
    // 定时任务的schedule
    private McJobSchedule       mcJobSchedule = null;
    private Map<String, Task>   tasks         = new HashMap<String, Task>();
    private Configuretion       configuretion;

    public RetryServer(){
        super();

        if (configuretion == null) {
            configuretion = new Configuretion();
            configuretion.loadDefault();
        }
    }

    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public void init() {
        // Load configuretion
        configuretion.loadResouceFromXml();

        // init zookeeper configure
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.setConfiguretion(configuretion);
        defaultConfiguration.init();

        // 获取所有Task配置
        Map tasksMap = applicationContext.getBeansOfType(Task.class);
        Collection<Task> values = tasksMap.values();
        for (Task task : values) {
            if (task != null) {
                buildTask(task);
                tasks.put(task.getTaskName(), task);
                logger.warn("Retry task find:" + task.getTaskName());
            }
        }

        if (scanners == null) {
            throw new NullPointerException("scanner can't be null!");
        }

        for (RetryScanner scanner : scanners) {
            // Set default appcode
            if (StringUtils.isBlank(scanner.getAppCode())) {
                scanner.setAppCode(configuretion.getString(Constants.RETRY_SYS_APP_CODE));
            }
            scanner.setServer(this);
        }

        try {
            mcJobSchedule.init();
        } catch (McJobScheduleException e) {
            logger.error("Init jobSchedule exception", e);
        }

        status = INITED.getStatus();
    }

    public void start() {
        if (status == INITED.getStatus() || status == STOPED.getStatus()) {
            for (RetryScanner scanner : scanners) {
                try {
                    mcJobSchedule.registerJob(scanner);
                } catch (IllegalArgumentException e) {
                    logger.error("Register error", e);
                } catch (McJobScheduleException e) {
                    logger.error("Register error", e);
                }
            }
            status = RUNNING.getStatus();

            try {
                mcJobSchedule.start();
            } catch (McJobScheduleException e) {
                logger.error("Start jobSchedule error", e);
            }
        }

    }

    public void stop() {
        if (status == RUNNING.getStatus()) {
            for (RetryScanner scanner : scanners) {
                try {
                    mcJobSchedule.pauseJob(scanner);
                } catch (IllegalArgumentException e) {
                    logger.error("Register error", e);
                } catch (McJobScheduleException e) {
                    logger.error("Register error", e);
                }
            }
            try {
                mcJobSchedule.stop();
            } catch (McJobScheduleException e) {
                logger.error("Stop schedule exception.", e);
            }
            status = STOPED.getStatus();
        }
    }

    public void destroy() {
        if (status == RUNNING.getStatus() || status == STOPED.getStatus()) {
            for (RetryScanner scanner : scanners) {
                try {
                    mcJobSchedule.unRegisterJob(scanner);
                } catch (IllegalArgumentException e) {
                    logger.error("Register error", e);
                } catch (McJobScheduleException e) {
                    logger.error("Register error", e);
                }
            }
            status = DESTROYED.getStatus();
        }
    }

    public int getStatus() {
        return status;
    }

    /**
     * 获取当前正在运行的job(提交的job不一定正在运行)
     * 
     * @return
     */
    public List<McJobDefinition> getRunningJob() {
        if (mcJobSchedule != null) {
            try {
                return mcJobSchedule.getCurrentRunningJob();
            } catch (McJobScheduleException e) {
                logger.error("Get running job exception", e);
            }
        }
        return null;
    }

    /**
     * 获取仓库中保存的job（包括所有注册的job，指所有注册了但未结束的job）
     * 
     * @return
     */
    public List<McJobDefinition> getStoredJob() {
        if (mcJobSchedule != null) {
            try {
                return mcJobSchedule.getCurrentStoreJob();
            } catch (McJobScheduleException e) {
                logger.error("Get store job exception", e);
            }
        }
        return null;
    }

    public void setScanners(List<RetryScanner> scanners) {
        this.scanners = scanners;
    }

    public List<RetryScanner> getScanners() {
        return scanners;
    }

    public void setMcJobSchedule(McJobSchedule mcJobSchedule) {
        this.mcJobSchedule = mcJobSchedule;
    }

    public McJobSchedule getMcJobSchedule() {
        return mcJobSchedule;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<String, Task> getTasks() {
        return Collections.unmodifiableMap(tasks);
    }

    public void setConfiguretion(Configuretion configuretion) {
        this.configuretion = configuretion;
    }

    public Configuretion getConfiguretion() {
        return configuretion;
    }

    public void addConf(String classpathUrl) {
        if (configuretion != null) {
            configuretion.addConf(classpathUrl);
        }
    }

    private void buildTask(Task task) {
        if (task != null) {
            if (task.getRetryTime() <= 0) {
                task.setRetryTime(configuretion.getShort(RETRY_SYS_COUNT));
            }
            if (task.getRetryPeriod() <= 0) {
                task.setRetryPeriod(configuretion.getLong(RETRY_SYS_PERIOD_MILLISECOND));
            }
            if (task.getRetryFactor() <= 0) {
                task.setRetryFactor(configuretion.getInt(RETRY_SYS_COUNT));
            }
            if (task.getExecuteTimeOut() <= 0) {
                task.setExecuteTimeOut(configuretion.getLong(RETRY_SYS_EXECUTE_TIMEOUT));
            }
            if (StringUtils.isBlank(task.getAppCode())) {
                task.setAppCode(configuretion.getString(RETRY_SYS_APP_CODE));
            }
        }
    }
}
