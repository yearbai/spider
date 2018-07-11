package com.moye.crawler.config;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * Created by guozp on 2017/8/28.
 *
 */
//@Component
@Configuration
public class TxConfigBeanName {
  @Autowired
  private DataSourceTransactionManager transactionManager;
  // 创建事务通知
  @Bean(name = "txAdvice")
  public TransactionInterceptor getAdvisor() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("*", "readOnly");
    properties.setProperty("do*", "PROPAGATION_REQUIRED");
    properties.setProperty("save*", "PROPAGATION_REQUIRED");
    properties.setProperty("insert*", "PROPAGATION_REQUIRED");
    properties.setProperty("add*", "PROPAGATION_REQUIRED");
    properties.setProperty("remove*", "PROPAGATION_REQUIRED");
    properties.setProperty("modify*", "PROPAGATION_REQUIRED");
    properties.setProperty("update*", "PROPAGATION_REQUIRED");
    properties.setProperty("del*", "PROPAGATION_REQUIRED");
    TransactionInterceptor tsi = new TransactionInterceptor(transactionManager,properties);
    return tsi;
  }
  @Bean
  public BeanNameAutoProxyCreator txProxy() {
    BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
    creator.setInterceptorNames("txAdvice");
    creator.setBeanNames("*Service", "*ServiceImpl");
    creator.setProxyTargetClass(true);
    return creator;
  }
}