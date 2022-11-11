package com.planetgallium.database;

// aka "column"
public class Field {

    private final String name;
    private final DataType dataType;
    private int limit;
    private Object value;

    public Field(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public Field(String name, DataType dataType, Object value) {
        this(name, dataType);
        this.value = value;
    }

    public Field(String name, DataType dataType, Object value, int limit) {
        this(name, dataType, value);
        this.limit = limit;
    }

    public Field(String name, DataType dataType, int limitOrValue) {
        // NOTE: this is a "double-constructor", can work for int data types and strings, but doing different things
        this(name, dataType);
        if (dataType == DataType.INTEGER || dataType == DataType.FLOAT) {
            this.value = limitOrValue;
        } else if (dataType == DataType.STRING || dataType == DataType.FIXED_STRING) {
            this.limit = limitOrValue;
        }
    }

    public String getSQLDataType() {
        switch (this.dataType) {
            case FIXED_STRING: return "CHAR";
            case STRING: return "VARCHAR";
            case INTEGER: return "INT";
            case FLOAT: return "FLOAT";
        }
        return null;
    }

    public String getName() { return name; }

    public DataType getDataType() { return dataType; }

    public int getLimit() { return limit; }

    public Object getValue() { return value; }

}
