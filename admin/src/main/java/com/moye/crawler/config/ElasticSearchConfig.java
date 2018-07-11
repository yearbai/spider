package com.moye.crawler.config;

import com.moye.crawler.modules.spider.service.impl.SpiderTaskServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Component
@Configuration
public class ElasticSearchConfig implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {
    private Logger logger = LogManager.getLogger(ElasticSearchConfig.class);
//    /**
//     * es集群地址
//     */
//    @Value("${es.cluster.name}")
//    private String hostName;
//    /**
//     * 端口
//     */
//    @Value("${elasticsearch.port}")
//    private String port;

    @Value("${es.cluster.nodes}")
    private String clusterNodes ;
    /**
     * 集群名称
     */
    @Value("${es.cluster.name}")
    private String clusterName;

    /**
     * 连接池
     */
    @Value("${es.cluster.pool}")
    private String poolSize;

    private TransportClient client;

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing elasticSearch client");
            if (client != null) {
                client.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public TransportClient getObject() throws Exception {
        return client;
    }

    @Override
    public Class<TransportClient> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("Building ElasticSearch client");
        // 配置信息
        Settings esSetting = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", false)// 增加嗅探机制，找到ES集群
                .put("thread_pool.search.size", Integer.parseInt(poolSize))// 增加线程池个数，暂时设为5
                .build();
        client = new PreBuiltTransportClient(esSetting);
        Map<String, Integer> nodeMap = parseNodeIpInfo();
        for (Map.Entry<String, Integer> entry : nodeMap.entrySet()) {
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(entry.getKey()), entry.getValue()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String, Integer> parseNodeIpInfo() {
        String[] nodeIpInfoArr = clusterNodes.split(",");
        Map<String, Integer> map = new HashMap<String, Integer>(nodeIpInfoArr.length);
        for (String ipInfo : nodeIpInfoArr) {
            String[] ipInfoArr = ipInfo.split(":");
            map.put(ipInfoArr[0], Integer.parseInt(ipInfoArr[1]));
        }
        return map;
    }


}
