package com.moye.crawler.common.constant;


import com.moye.crawler.common.utils.ResultCode;

public class CodeConstant {

    public static final ResultCode CSRF_ERROR = new ResultCode("101", "CSRF ERROR:无效的token，或者token过期");

    public static final String SPIDER_TASK_MAP_KEY = "spider_task_key";

    public static final String SPIDERINFO_KEY = "spider_info_";
    public static final String FINISH_LIST = "spider_finish_list";

    public static final String INDEX_NAME = "commons";
    public static final String TYPE_NAME = "webpage";


}
