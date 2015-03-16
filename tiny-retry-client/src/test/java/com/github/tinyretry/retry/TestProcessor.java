package com.github.tinyretry.retry;

import java.io.Serializable;

import com.github.tinyretry.retry.domain.ProcessContext;
import com.github.tinyretry.retry.domain.ProcessResult;
import com.github.tinyretry.retry.domain.Processor;
import com.github.tinyretry.retry.domain.Task;

public class TestProcessor extends Task implements Processor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TestProcessor(){
        this.setProcessor(this);
    }

    @Override
    public ProcessResult execute(ProcessContext context) {
        // 通过context获取data. data就是retryPorcessClient.async()方法的第二个参数
        Serializable data = context.getData();

        System.out.println("receive data: ---> " + data);

        return ProcessResult.SUCCESS;
    }

}
