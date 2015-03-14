package com.github.tinyretry.retry;

import java.util.List;

import com.github.tinyretry.retry.domain.McTaskDO;
import com.github.tinyretry.retry.domain.McTaskHistoryDO;
import com.github.tinyretry.retry.domain.TaskHistoryQuery;
import com.github.tinyretry.retry.domain.TaskQuery;
import com.github.tinyretry.retry.domain.TaskUpdate;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.retry.service.RetryTaskService;

public class MockRetryTaskService implements RetryTaskService {

    @Override
    public List<McTaskDO> queryMcTaskList(TaskQuery taskQuery) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int countMcTaskList(TaskQuery taskQuery) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public McTaskDO insertMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
        mcTaskDO.setId(1L);
        return mcTaskDO;
    }

    @Override
    public Integer updateMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
        return 1;
    }

    @Override
    public Integer updateMcTaskStatus(TaskUpdate taskUpdate) throws RetryRemoteException {
        return 1;
    }

    @Override
    public Integer deleteMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
        return 1;
    }

    @Override
    public List<McTaskHistoryDO> queryMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int countMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long insertMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer updateMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer deleteMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
