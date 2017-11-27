package ru.javaops.masterjava.persist.model.type;

public enum GroupType {
    REGISTERING,
    CURRENT,
    FINISHED;

    public static GroupType fromValue(String v) {
        return valueOf(v);
    }
}
