package com.github.tinyretry.retry;

import static com.github.tinyretry.retry.constants.Constants.*;
import static com.github.tinyretry.retry.constants.RetryServerStatus.*;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.commons.model.LifeCycle;
import com.github.tinyretry.retry.conf.Configureable;
import com.github.tinyretry.retry.conf.Configuretion;
import com.github.tinyretry.retry.conf.DefaultConfiguration;
import com.github.tinyretry.retry.constants.Constants;
import com.github.tinyretry.retry.domain.ProcessResult;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.retry.scanner.RetryScanner;
import com.github.tinyretry.retry.service.RetryTaskService;
import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.domain.McJobGroup;
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
public class RetryCli implements LifeCycle, Configureable, ApplicationContextAware {

    private static final Logger logger        = LoggerFactory.getLogger(RetryCli.class);
    private ApplicationContext  applicationContext;
    private ApplicationContext  parentContext;
    private volatile int        status        = BORN.getStatus();
    private List<RetryScanner>  scanners;
    // 定时任务的schedule
    private McJobSchedule       mcJobSchedule = null;
    private Map<String, Task>   tasks         = new HashMap<String, Task>();
    private Configuretion       configuretion;

    private AsyncProcessor      processor;

    public RetryCli(){
        super();

        if (configuretion == null) {
            configuretion = new Configuretion();
            configuretion.loadDefault();
        }
    }

    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public void init() {

        applicationContext = new ClassPathXmlApplicationContext(
                                                                new String[] { "classpath:default-asyntask-config.xml" },
                                                                parentContext);

        // Load configuretion
        configuretion.loadResouceFromXml();

        // init zookeeper configure
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.setConfiguretion(configuretion);
        defaultConfiguration.init();

        // 初始化所有的group
        Map<String, McJobGroup> jobGroups = getBeansOfType(McJobGroup.class);
        if (jobGroups != null) {
            mcJobSchedule.addGroups(new ArrayList<McJobGroup>(jobGroups.values()));
        }

        // 获取所有Task配置
        Map tasksMap = getBeansOfType(Task.class);
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

        Map<String, RetryTaskService> retryTaskServices = getBeansOfType(RetryTaskService.class);

        if (retryTaskServices == null || retryTaskServices.isEmpty()) {
            throw new NullPointerException("retryTaskServices can't be null!");
        }

        RetryTaskService retryTaskService = retryTaskServices.values().iterator().next();

        for (RetryScanner scanner : scanners) {
            // Set default appcode
            if (StringUtils.isBlank(scanner.getAppCode())) {
                scanner.setAppCode(configuretion.getString(Constants.RETRY_SYS_APP_CODE));
            }
            scanner.setAsyncProcessor(processor);
            scanner.setRetryTaskService(retryTaskService);
            scanner.setRetryMcJobSchedule(mcJobSchedule);
            scanner.setServer(this);
        }

        try {
            mcJobSchedule.init();
        } catch (McJobScheduleException e) {
            logger.error("Init jobSchedule exception", e);
        }

        // init processor
        processor = new AsyncProcessor();

        processor.setRetryMcJobSchedule(mcJobSchedule);
        processor.setRetryTaskService(retryTaskService);
        processor.init();

        status = INITED.getStatus();
    }

    private <T> Map<String, T> getBeansOfType(Class<T> tClass) {
        Map<String, T> returnType = applicationContext.getBeansOfType(tClass);
        if (returnType == null || returnType.isEmpty()) {
            return applicationContext.getParent().getBeansOfType(tClass);
        }

        return returnType;
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

    public void addTask(Task task) {
        if (task == null || StringUtils.isBlank(task.getTaskName())) {
            return;
        }

        tasks.put(task.getTaskName(), task);
    }

    public void removeTask(String taskName) {
        tasks.remove(taskName);
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
        this.parentContext = applicationContext;
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

    // ==========client =====================

    /**
     * <pre>
     * （推荐）异步执行重试任务 
     *  会在本地注册任务并启动,失败后才会倍分配到集群任一台服务器执行
     * </pre>
     *
     * @param task 异步处理的任务
     * @param data 任务的数据
     * @throws McJobScheduleException
     * @throws com.github.tinyretry.retry.exception.RetryRemoteException
     */
    public void async(Task task, Serializable data) throws McJobScheduleException, RetryRemoteException {
        validate();
        processor.execute(task, data);
    }

    /**
     * <pre>
     * （推荐）异步执行重试任务 
     *  会在本地注册任务并启动,失败后才会倍分配到集群任一台服务器执行
     * </pre>
     *
     * @param task 异步处理的任务
     * @param data 任务的数据
     * @param callbacks 执行结束时回调,但只会在首次执行,如果有重试时也需要的,请重载{@code ReTryDispatcher}
     * @throws McJobScheduleException
     * @throws RetryRemoteException
     */
    public void async(Task task, Serializable data, com.github.commons.model.Observer<ProcessResult>... callbacks)
                                                                                                                  throws McJobScheduleException,
                                                                                                                  RetryRemoteException {

        validate();
        processor.execute(task, data, callbacks);
    }

    /**
     * <pre>
     * 异步远程执行重试任务 
     * 任务不会在本地立马执行，需要调度器远程取得任务后执行 会对数据库造成一定压力，慎用！！！
     * </pre>
     *
     * @param task 异步处理的任务
     * @param data 任务的数据
     * @throws McJobScheduleException
     * @throws RetryRemoteException
     */
    public void asyncRemote(Task task, Serializable data) throws McJobScheduleException, RetryRemoteException {
        validate();
        processor.execWithOutRegisterJob(task, data);
    }

    private void validate() {
        if (this.getStatus() != RUNNING.getStatus()) {
            throw new IllegalStateException(" Retry container not running.");
        }
    }
}
