package com.onoprienko.ioc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Bean {
    private final String id;
    private Object value;
}
