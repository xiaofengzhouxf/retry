package com.github.tinyretry.retry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.commons.model.Observable;
import com.github.commons.model.Observer;
import com.github.tinyretry.retry.constants.TaskStatus;
import com.github.tinyretry.retry.domain.McTaskDO;
import com.github.tinyretry.retry.domain.ProcessResult;
import com.github.tinyretry.retry.domain.RetryJob;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.retry.service.RetryTaskService;
import com.github.tinyretry.retry.tools.RetryJobTools;
import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.domain.McJobGroup;

/**
 * <pre>
 * desc: 异步任务处理类
 * 
 * created: 2012-8-23 下午02:41:29
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class AsyncProcessor implements Observer<ProcessResult> {

    private static final Logger          logger             = LoggerFactory.getLogger(AsyncProcessor.class);

    // 重试框架单独的mcJobSchedule
    private McJobSchedule                retryMcJobSchedule = null;

    private ConcurrentLinkedQueue<Entry> cacheQueue         = new ConcurrentLinkedQueue<Entry>();

    private RetryTaskService             retryTaskService;

    // 是否异步添加任务,对于重要的任务来说，这配置会有风险
    private boolean                      memoryAble         = true;

    /**
     * 调用初始化方法,默认启动内存缓存区模式
     */
    public void init() {

        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        newSingleThreadExecutor.submit(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Entry entry = cacheQueue.poll();
                        if (entry != null) {
                            register(entry.getTask(), entry.getData(), entry.getNextTime());
                        } else {
                            Thread.sleep(500);
                        }
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
        });
    }

    /**
     * 执行异步任务
     * 
     * @param task 需要执行的任务
     * @param data 需要执行的任务的数据
     * @param callbacks 回调函数，任务执行结束时回调，仅限第一次调用，超时重试无法调用到
     * @throws RetryRemoteException 远程服务失败，任务没有记录成功，该异常说明整个都失败了
     * @throws McJobScheduleException 注册本地job失败,还可以等待超时处理,可以选择忽略
     */
    public void execute(Task task, Serializable data, Observer<ProcessResult>... callbacks)
                                                                                           throws RetryRemoteException,
                                                                                           McJobScheduleException {
        // ==========0. check pararm==========
        checkParams(task);

        // ==========1. build pararm==========
        // clone一个避免并发问题
        Task cloneTask = cloneTask(task);
        long calculateNextTime = calculateNextTime(cloneTask);
        // ==========2. build db record==========
        logger.info("Task submit: " + task.getTaskId() + " name: " + cloneTask.getTaskName());
        if (cloneTask.getTaskId() <= 0) {
            cloneTask = buildTask(cloneTask, TaskStatus.DEALING, calculateNextTime, data);
        }

        if (!memoryAble) {
            // ==========3. create job==========
            register(cloneTask, data, calculateNextTime, callbacks);
        } else {
            cacheQueue.add(new Entry(cloneTask, data, calculateNextTime));
        }
    }

    private void register(Task task, Serializable data, long calculateNextTime, Observer<ProcessResult>... callbacks)
                                                                                                                     throws McJobScheduleException {
        RetryJob job = RetryJobTools.generateMcJob(RetryJobTools.generateMcJobDefinition(RetryJobTools.generateJobName(task),
                                                                                         RetryJobTools.generateJobName(task),
                                                                                         task.getDescription()));
        job.setProcessor(task.getProcessor());
        // add job observer
        job.addObserver(this);
        if (callbacks != null && callbacks.length > 0) {
            for (Observer<ProcessResult> observer : callbacks) {
                job.addObserver(observer);
            }
        }
        job.setTask(task);
        job.setData(data);

        McJobGroup jobGroup = RetryJobTools.generateMcJobGroup(RetryJobTools.generateJobName(task), calculateNextTime);

        List<McJobGroup> groups = new ArrayList<McJobGroup>(1);
        groups.add(jobGroup);

        // ==========4. run the job immadiately==========
        logger.info("Register job:" + task.getTaskId() + " name: " + task.getTaskName());
        retryMcJobSchedule.setGroups(groups);
        retryMcJobSchedule.init();
        retryMcJobSchedule.registerJob(job);
        // 重复启动没关系
        retryMcJobSchedule.start();
    }

    /**
     * 执行异步任务，该接口会创建一个数据库任务，但是不会在本地注册job 只有重试周期很长的任务才适合用该方法，这会增加数据库压力，慎用!!!
     * 
     * @param task 需要执行的任务
     * @param data 需要执行的任务的数据
     * @throws RetryRemoteException 远程服务失败，任务没有记录成功，该异常说明整个都失败了
     * @throws McJobScheduleException 注册本地job失败,还可以等待超时处理,可以选择忽略
     */
    public void execWithOutRegisterJob(Task task, Serializable data) throws RetryRemoteException,
                                                                    McJobScheduleException {
        // ==========0. check pararm==========
        checkParams(task);

        // ==========1. build pararm==========
        // clone一个避免并发问题
        Task cloneTask = cloneTask(task);
        long calculateNextTime = calculateNextTime(cloneTask);

        // ==========2. build db record==========
        logger.info("Task submit: " + task.getTaskId());
        if (cloneTask.getTaskId() <= 0) {
            cloneTask = buildTask(cloneTask, TaskStatus.WAITING, calculateNextTime, data);
        }
    }

    public void update(Observable<ProcessResult> observable, ProcessResult result) {

        if (observable != null && observable instanceof RetryJob && ((RetryJob) observable).getTask() != null) {
            RetryJob job = (RetryJob) observable;
            job.setFinlshedTime(System.currentTimeMillis());
            long taskId = job.getTask().getTaskId();

            if (result != null) {
                McTaskDO mcTaskDO = new McTaskDO();
                mcTaskDO.setId(taskId);
                short status = TaskStatus.SUCCESS.getStatus();
                if (!result.isSuccess()) {
                    if (job.getTask().getCurrentTime() < job.getTask().getRetryTime()) {
                        status = TaskStatus.WAITING.getStatus();
                    } else {
                        status = TaskStatus.FAIL.getStatus();
                        // if failed, will update the data
                        mcTaskDO.setNextTime(calculateNextTime(job.getTask()));
                    }

                    mcTaskDO.setFailReason(result.getFailReason());

                    if (result.isUpdateData()) {
                        mcTaskDO.setContext(generateByteArrayData(result.getData()));
                    }

                    mcTaskDO.setStatus(status);
                    // 1. record the result
                    try {
                        // update the new processor
                        mcTaskDO.setProcessor(RetryJobTools.getIPAddress());
                        retryTaskService.updateMcTask(mcTaskDO);
                    } catch (RetryRemoteException e) {
                        logger.error("Update task status exception", e);
                        // continue unlock zk to preventing deadlock
                    }

                    logger.error("fail deal job" + taskId + "-" + job.getTask().getAppCode() + "-"
                                 + job.getTask().getTaskName() + "--->" + job.getElapsedTime() + " -- "
                                 + job.getActualRunTime() + " -- " + job.successRate());
                } else {
                    // 考虑到数据库压力，直接删除
                    try {
                        retryTaskService.deleteMcTask(mcTaskDO);
                        logger.error("Success deal task and deleted:" + taskId + "-" + job.getTask().getAppCode() + "-"
                                     + job.getTask().getTaskName() + "--->" + job.getElapsedTime() + " -- "
                                     + job.getActualRunTime() + " -- " + job.successRate());
                    } catch (RetryRemoteException e) {
                        logger.error("delete task status exception", e);
                    }
                }
            }
        }
    }

    private byte[] generateByteArrayData(Serializable data) {
        return SerializationUtils.serialize(data);
    }

    private Task buildTask(Task task, TaskStatus jobStatus, long calculateNextTime, Serializable data)
                                                                                                      throws RetryRemoteException {
        McTaskDO mcTaskDO = new McTaskDO();
        mcTaskDO.setAppCode(task.getAppCode());
        mcTaskDO.setNextTime(calculateNextTime);
        mcTaskDO.setPriority(task.getPriority() >= 0 ? task.getPriority() : 0);
        mcTaskDO.setRetryTime(task.getRetryTime() >= 0 ? task.getRetryTime() : 0);
        mcTaskDO.setStatus(jobStatus.getStatus());
        mcTaskDO.setType(task.getTaskName());
        mcTaskDO.setVersion((short) 1);
        mcTaskDO.setContext(generateByteArrayData(data));
        mcTaskDO.setProcessor(RetryJobTools.getIPAddress());
        mcTaskDO = retryTaskService.insertMcTask(mcTaskDO);
        task.setTaskId(mcTaskDO.getId());
        logger.info("New task :" + task.getTaskId());
        return task;
    }

    private Task cloneTask(Task task) {
        return new Task(task);
    }

    private long calculateNextTime(Task task) {
        long nextTime = task.getNextTime();
        if (nextTime <= 0) {
            nextTime = RetryJobTools.calculateNextTime(task.getRetryPeriod(), task.getRetryFactor(),
                                                       task.getCurrentTime() > 0 ? task.getCurrentTime() - 1 : 0);
        }
        return nextTime;
    }

    private void checkParams(Task task) throws RetryRemoteException {
        if (task == null) {
            throw new RetryRemoteException("param task can't be null");
        }

        if (StringUtils.isBlank(task.getAppCode())) {
            throw new RetryRemoteException("param appcode can't be blank");
        }

        if (StringUtils.isBlank(task.getTaskName())) {
            throw new RetryRemoteException("param taskname can't be blank");
        }
    }

    public void setRetryMcJobSchedule(McJobSchedule retryMcJobSchedule) {
        this.retryMcJobSchedule = retryMcJobSchedule;
    }

    public int compareTo(ProcessResult o) {
        return 0;
    }

    public void setRetryTaskService(RetryTaskService retryTaskService) {
        this.retryTaskService = retryTaskService;
    }

    class Entry {

        Task         task;
        Serializable data;
        long         nextTime;

        public Entry(Task task, Serializable data, long nextTime){
            super();
            this.task = task;
            this.data = data;
            this.nextTime = nextTime;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Serializable getData() {
            return data;
        }

        public void setData(Serializable data) {
            this.data = data;
        }

        public long getNextTime() {
            return nextTime;
        }

        public void setNextTime(long nextTime) {
            this.nextTime = nextTime;
        }

    }
}
