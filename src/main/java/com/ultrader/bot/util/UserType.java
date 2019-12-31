package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

/**
 * User Type
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum  UserType {
    ADMIN("Admin", 1),
    OPERATOR("Operator", 2),
    READ_ONLY_USER("Read-Only User", 3);
    private String name;
    private Integer id;

    UserType(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public static UserType findById(final int id){
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
