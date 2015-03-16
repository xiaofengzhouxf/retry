package com.github.tinyretry.retry;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.tinyretry.retry.service.RetryTaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.retry.exception.RetryRemoteException;
import com.github.tinyretry.timer.McJobScheduleException;

public class RetryServerTest {

    private ClassPathXmlApplicationContext appContext = null;
    private RetryCli                       retryCli   = null;

    @Before
    public void init() {

        appContext = new ClassPathXmlApplicationContext(new String[] {"classpath:default-retry-spring.xml",
                "classpath:test-tasks.xml" });

        retryCli = appContext.getBean("retryServer", RetryCli.class);

        Map<String, RetryTaskService> retryTaskServices = appContext.getBeansOfType(RetryTaskService.class);

        if (retryTaskServices == null || retryTaskServices.isEmpty()) {
            throw new NullPointerException("retryTaskServices can't be null!");
        }

        retryCli.init();
        retryCli.start();
    }

    @Test
    public void test() throws BeansException, McJobScheduleException, RetryRemoteException, InterruptedException {

        retryCli.async(appContext.getBean("testTaskProcessor", Task.class), "this is a test");

        TimeUnit.SECONDS.sleep(1);
    }

    @After
    public void after() {
        retryCli.destroy();
    }

}
