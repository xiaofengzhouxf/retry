package com.github.tinyretry.retry.conf;

/**
 * <pre>
 * desc: 
 * created: 2012-9-4 ÏÂÎç01:07:24
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class DefaultConfiguration implements Configureable { // extends Configration implements Configureable {

    private Configuretion configuretion;

    public void init() {

    }

    // @Override
    // public void init() {
    // if (configuretion != null) {
    // Configration.ZOOKEEPER_CONNECT_STR = configuretion.getString(Constants.ZOOKEEPER_CONNECT_STR);
    // Configration.ZOOKEEPER_CONNECT_TIME_OUT = configuretion.getInt(Constants.ZOOKEEPER_CONNECT_TIME_OUT);
    // Configration.ZOOKEEPER_CONNECT_RETRY_TIMES = configuretion.getInt(Constants.ZOOKEEPER_CONNECT_RETRY_TIMES);
    // Configration.ZOOKEEPER_CONNECT_RETRY_INTERVAL_TIME = configuretion
    // .getInt(Constants.ZOOKEEPER_CONNECT_RETRY_INTERVAL_TIME);
    // Configration.ZOOKEEPER_EXECUTE_CONNECT_TIMEOUT = configuretion.getInt(Constants.ZOOKEEPER_CONNECT_TIME_OUT);
    // Configration.DEFAULT_PARENT = configuretion.getString(Constants.ZOOKEEPER_DEFAULT_PARENT);
    // }
    // }

    public void addConf(String classpathUrl) {
        if (configuretion != null) {
            configuretion.addConf(classpathUrl);
        }
    }

    public void setConfiguretion(Configuretion configuretion) {
        this.configuretion = configuretion;
    }

}
