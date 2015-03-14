package com.github.tinyretry.retry.service;

import java.util.List;

import com.github.tinyretry.retry.domain.McTaskDO;
import com.github.tinyretry.retry.domain.McTaskHistoryDO;
import com.github.tinyretry.retry.domain.TaskHistoryQuery;
import com.github.tinyretry.retry.domain.TaskQuery;
import com.github.tinyretry.retry.domain.TaskUpdate;
import com.github.tinyretry.retry.exception.RetryRemoteException;

/**
 * <pre>
 * desc: 重试任务记录操作服务
 * created: 2012-9-5 上午10:43:31
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public interface RetryTaskService {

	public List<McTaskDO> queryMcTaskList(TaskQuery taskQuery) throws RetryRemoteException;

	public int countMcTaskList(TaskQuery taskQuery) throws RetryRemoteException;

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public McTaskDO insertMcTask(McTaskDO mcTaskDO) throws RetryRemoteException;

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer updateMcTask(McTaskDO mcTaskDO) throws RetryRemoteException;

	/**
	 * 
	 * @param mcTaskDO
	 * @return
	 * @throws RetryRemoteException
	 */
	public Integer updateMcTaskStatus(TaskUpdate taskUpdate) throws RetryRemoteException;

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer deleteMcTask(McTaskDO mcTaskDO) throws RetryRemoteException;

	
	
	
	//=======================history=========================================================
	/**
	 * select mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public List<McTaskHistoryDO> queryMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException;

	public int countMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException;

	/**
	 * insert into mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Long insertMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException;

	/**
	 * update mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer updateMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException;

	/**
	 * delete from mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer deleteMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException;
}
