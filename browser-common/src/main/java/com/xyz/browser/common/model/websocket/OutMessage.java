package com.xyz.browser.common.model.websocket;

//import com.xyz.browser.app.core.util.Json;
import com.xyz.browser.common.util.Json;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
public class OutMessage {

    private String retType;

    private Map<String, Object> result;

    public OutMessage(){}

    public OutMessage(String retType){
        this.retType = retType;
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
    public OutMessage addData(String name, Object value) {
        initData();
        result.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public OutMessage addData(Object value) {
        if (value != null) {
            initData();
            result.putAll(Json.toObject(Json.toJson(value), Map.class));
        }
        return this;
    }


}
