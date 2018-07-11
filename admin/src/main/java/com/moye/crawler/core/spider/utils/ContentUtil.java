package com.moye.crawler.core.spider.utils;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/16 17:04
 * @modified By
 */
public class ContentUtil {

    public static String format(String content){
        content = content.replaceAll("<script([\\s\\S]*?)</script>", "");
        content = content.replaceAll("<style([\\s\\S]*?)</style>", "");
        content = content.replace("</p>", "***");
        content = content.replace("<BR>", "***");
        content = content.replaceAll("<([\\s\\S]*?)>", "");

        content = content.replace("***", "<br/>");
        content = content.replace("\n", "<br/>");
        content = content.replaceAll("(\\<br/\\>\\s*){2,}", "<br/> ");
        content = content.replaceAll("(&nbsp;\\s*)+", " ");
        return content;
    }
}
