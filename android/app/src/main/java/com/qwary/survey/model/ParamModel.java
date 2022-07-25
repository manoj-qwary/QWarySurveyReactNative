package com.qwary.survey.model;

import java.io.Serializable;

public class ParamModel implements Serializable {
    String key;
    String value;

    public ParamModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
