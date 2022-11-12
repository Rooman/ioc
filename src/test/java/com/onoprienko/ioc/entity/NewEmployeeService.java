package com.onoprienko.ioc.entity;

import com.onoprienko.ioc.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewEmployeeService {
    private String employee;
    private int salary;

    @PostConstruct
    public void init() {
        this.salary = salary + 200;
    }

    public void work() {
        System.out.println("Working");
    }
}
