package com.moye.crawler.base.annotion;

import java.lang.annotation.*;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/18 22:46
 * @Modified By
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BussinessLog {


    /**
     * 业务的名称,例如:"修改菜单"
     */
    String value() default "";

    /**
     * 被修改的实体的唯一标识,例如:菜单实体的唯一标识为"id"
     */
    String key() default "id";

}
