package com.github.tinyretry.retry.client;

import java.io.Serializable;

import com.github.commons.model.Observer;
import com.github.tinyretry.retry.AsyncProcessor;
import com.github.tinyretry.retry.domain.ProcessResult;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.timer.McJobScheduleException;

/**
 * <pre>
 * desc: 重试处理客户端
 * 
 * 使用方法：
 * 
 * 1. 创建{@linkplain com.github.tinyretry.retry.domain.Task Task}实例testTask，
 * 2. 调用retryPorcessClient.async(testTask, "asdf"),第二个参数是任务执行需要用到的数据
 * 
 * 
 * 
 * created: 2012-9-3 上午11:58:13
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class RetryPorcessClient {

    private AsyncProcessor asyncProcessor;

    /**
     * <pre>
     * （推荐）异步执行重试任务 
     *  会在本地注册任务并启动,失败后才会倍分配到集群任一台服务器执行
     * </pre>
     * 
     * @param task 异步处理的任务
     * @param data 任务的数据
     * @throws McJobScheduleException
     * @throws RetryRemoteException
     */
    public void async(Task task, Serializable data) throws McJobScheduleException, RetryRemoteException {
        asyncProcessor.execute(task, data);
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
    public void async(Task task, Serializable data, Observer<ProcessResult>... callbacks)
                                                                                         throws McJobScheduleException,
                                                                                         RetryRemoteException {
        asyncProcessor.execute(task, data, callbacks);
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
        asyncProcessor.execWithOutRegisterJob(task, data);
    }

    public void setAsyncProcessor(AsyncProcessor asyncProcessor) {
        this.asyncProcessor = asyncProcessor;
    }

}
