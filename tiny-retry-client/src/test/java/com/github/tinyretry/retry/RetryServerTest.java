package com.github.tinyretry.retry;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.tinyretry.retry.client.RetryPorcessClient;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.timer.McJobScheduleException;

public class RetryServerTest {

    private ClassPathXmlApplicationContext appContext = null;
    private RetryServer                    server     = null;
    private RetryPorcessClient             retryPorcessClient;

    @Before
    public void init() {

        appContext = new ClassPathXmlApplicationContext(new String[] { "classpath:retry-spring.xml",
                "classpath:test-tasks.xml" });

        server = appContext.getBean("retryServer", RetryServer.class);

        retryPorcessClient = appContext.getBean("retryPorcessClient", RetryPorcessClient.class);

        server.init();
        server.start();
    }

    @Test
    public void test() throws BeansException, McJobScheduleException, RetryRemoteException, InterruptedException {

        retryPorcessClient.async(appContext.getBean("testTask", Task.class), "this is a test");

    }

    @After
    public void after() {
        server.destroy();
    }

}
