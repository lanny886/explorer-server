package com.xyz.browser.common.model.websocket;

import com.xyz.browser.common.util.Json;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class InMessage {

    private String reqType;

    private Map<String, Object> content;

    private String toUser;

    public InMessage(){}

    public InMessage(String reqType){
        this.reqType = reqType;
    }
    public InMessage(String reqType,String toUser){
        this.reqType = reqType;
        this.toUser = toUser;
    }
    /**
     * 返回json对象
     *
     * @return
     */
    public String toJson() {
        return Json.toJson(this);
    }


    private void initData() {
        if (content == null) {
            content = new HashMap<>();
        }
    }

    /**
     * 加入数据项
     *
     * @param name
     * @param value
     */
    public InMessage addData(String name, Object value) {
        initData();
        content.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public InMessage addData(Object value) {
        if (value != null) {
            initData();
            content.putAll(Json.toObject(Json.toJson(value), Map.class));
        }
        return this;
    }

}
