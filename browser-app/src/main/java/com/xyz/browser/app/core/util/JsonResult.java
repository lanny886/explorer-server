package com.xyz.browser.app.core.util;

import com.xyz.browser.app.core.common.exception.BizExceptionEnum;
import com.xyz.browser.common.util.Json;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wuchenghua
 */
public class JsonResult {

    /** 错误码 */
    private int ret = 0;

    /**
     * 消息
     */
    private String msg = "";

    /**
     * 数据
     */
    private Map<String, Object> result;

    public JsonResult() {
    }

    public JsonResult(int ret) {
        this.ret = ret;
    }

    public JsonResult(int ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    public JsonResult(BizExceptionEnum statusCode){
        this.ret = statusCode.getCode();
        this.msg = statusCode.getMessage();
    }

    /**
     * 返回json对象
     *
     * @return
     */
    public String toJson() {
        return Json.toJson(this);
    }

    /**
     * 直接写入客户端
     *
     * @param response
     * @throws IOException
     */
    public void toJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(toJson());
    }

    private void initData() {
        if (result == null) {
            result = new HashMap<>();
        }
    }

    /**
     * 加入数据项
     *
     * @param name
     * @param value
     */
    public JsonResult addData(String name, Object value) {
        initData();
        result.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JsonResult addData(Object value) {
        if (value != null) {
            initData();
            result.putAll(Json.toObject(Json.toJson(value), Map.class));
        }
        return this;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}