package com.zp4rker.localdb;

public class Column {

    public String name;
    public DataType dataType;
    public int limit = 0;
    private Object value;

    public Column(String name, DataType dataType, int limit) {
        this.name = name;
        this.dataType = dataType;
        this.limit = limit;
    }

    public Column(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.limit = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
