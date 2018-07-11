package com.moye.crawler.entity.spider;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
@TableName("spider_task")
public class SpiderTask extends Model<SpiderTask> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
	@TableId(value="id", type= IdType.INPUT)
	private String id;
    /**
     * 爬虫模板id
     */
	@TableField("spider_info_id")
	private Integer spiderInfoId;
    /**
     * 任务名称
     */
	private String name;
    /**
     * 任务状态
     */
	private String state;
    /**
     * 花费时间
     */
	@TableField("use_time")
	private Long useTime;
    /**
     * 采集数量
     */
	private Integer count = 0;
    /**
     * 任务创建时间
     */
	@TableField("create_time")
	private Date createTime;

	@TableField("callback_url")
	private String callbackUrl;

	@TableField("callback_para")
	private String callbackPara;

	private String desc;

	@TableField(exist = false)
	private SpiderInfo spiderInfo;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSpiderInfoId() {
		return spiderInfoId;
	}

	public void setSpiderInfoId(Integer spiderInfoId) {
		this.spiderInfoId = spiderInfoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getUseTime() {
		return useTime;
	}

	public void setUseTime(Long useTime) {
		this.useTime = useTime;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackPara() {
		return callbackPara;
	}

	public void setCallbackPara(String callbackPara) {
		this.callbackPara = callbackPara;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public SpiderInfo getSpiderInfo() {
		return spiderInfo;
	}

	public void setSpiderInfo(SpiderInfo spiderInfo) {
		this.spiderInfo = spiderInfo;
	}

	public void increaseCount() {
		this.count++;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SpiderTask{" +
			", id=" + id +
			", spiderInfoId=" + spiderInfoId +
			", name=" + name +
			", state=" + state +
			", useTime=" + useTime +
			", count=" + count +
			", createTime=" + createTime +
			"}";
	}
}
