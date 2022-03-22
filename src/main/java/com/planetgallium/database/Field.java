package com.planetgallium.database;

public class Field {

    // aka "column"

    private final String name;
    private final DataType dataType;
    private Object value;

    public Field(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.value = null;
    }

    public Field(String name, DataType dataType, Object value) {
        this(name, dataType);
        this.value = value;
    }

    public String getSQLDataType() {
        switch (this.dataType) {
            case STRING: return "VARCHAR";
            case INTEGER: return "INT";
            case FLOAT: return "FLOAT";
        }
        return null;
    }

    public String getName() { return name; }

    public DataType getDataType() { return dataType; }

    public Object getValue() { return value; }

}
