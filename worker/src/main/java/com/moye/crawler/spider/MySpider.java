package com.moye.crawler.spider;


import com.moye.crawler.config.SpiderProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/24 11:18
 * @Modified By
 */
public class MySpider extends Spider {

    private SpiderProperties spiderProperties;

    private Logger LOG = LogManager.getLogger(MySpider.class);

    private long start = System.currentTimeMillis();

    public MySpider(PageProcessor pageProcessor, SpiderProperties spiderProperties) {
        super(pageProcessor);
        this.spiderProperties = spiderProperties;
    }

    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);
        if(this.getPageCount() >= 20){
            System.out.println(this.getPageCount() + "完成");
            this.stop();
        }
    }

    @Override
    protected void onError(Request request) {
        super.onError(request);
        LOG.info("处理网页" + request.getUrl() + "时发生错误" + request.getExtras());
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        MySpider mySpider = (MySpider) o;
        return new EqualsBuilder()
                .append(this.getUUID(), mySpider.getUUID())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.getUUID())
                .toHashCode();
    }
}
