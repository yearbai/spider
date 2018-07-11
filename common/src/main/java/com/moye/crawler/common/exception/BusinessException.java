package com.moye.crawler.common.exception;


import com.moye.crawler.common.utils.ErrorCodeEnum;

/***
 * 自定义业务异常
 * @ClassName:BusinessException
 * @description:(描述)
 * @author:hujunhua
 * @date:2018年5月4日 上午11:37:48
 *
 */
public class BusinessException extends RuntimeException {

    /**
     * @Fields serialVersionUID:(描述)
     */
    private static final long serialVersionUID = 1L;

    private String errCode;
    private String errMsg;
    private Object data;

    public BusinessException(String errCode) {
        String errMsg = ErrorCodeEnum.getName( errCode );
        errMsg = errMsg == null ? "未知错误" : errMsg;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public BusinessException(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public BusinessException(String errCode, String errMsg, Object data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
