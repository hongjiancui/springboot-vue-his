package com.cui.springboot_his.Model;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/*接口返回对象*/
@Component
public class ResultMap extends HashMap<String, Object> {
    public ResultMap() {
    }

    public ResultMap success() {
        this.put("result", "success");
        return this;
    }

    public ResultMap fail() {
        this.put("result", "fail");
        return this;
    }

    public ResultMap message(Object message) {
        this.put("message", message);
        return this;
    }

    public ResultMap data(Object data) {
        this.put("data", data);
        return this;
    }
}