package com.shuvam.model;

import java.util.Map;

public class ClassDefinitionRequest {

    private String className;
    private Map<String, String> fields;

    // Getters and Setters
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Map<String, String> getFields() {
        return fields;
    }
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
