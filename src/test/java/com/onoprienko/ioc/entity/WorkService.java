package com.onoprienko.ioc.entity;

public class WorkService {
    private EmployeeService employeeService;

    public void work() {
        employeeService.work();
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
