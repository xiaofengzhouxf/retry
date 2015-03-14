package com.github.tinyretry.retry;

import java.io.Serializable;

import com.github.tinyretry.retry.domain.ProcessContext;
import com.github.tinyretry.retry.domain.ProcessResult;
import com.github.tinyretry.retry.domain.Processor;

public class TestProcessor implements Processor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public ProcessResult execute(ProcessContext context) {
        // 通过context获取data. data就是retryPorcessClient.async()方法的第二个参数
        Serializable data = context.getData();

        System.out.println("receive data: ---> "+data);

        return ProcessResult.SUCCESS;

    }
}
