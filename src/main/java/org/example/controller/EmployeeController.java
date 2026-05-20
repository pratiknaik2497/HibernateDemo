package org.example.controller;

import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public String showEmployees(Model model){
        model.addAttribute("employees", employeeService.get());
        return "employees";
    }

    @ResponseBody
    @PostMapping("/employee/add")
    public void addEmployee(@RequestBody Employee employee){
        employeeService.save(employee);
        System.out.println("Employee Added");
    }

    @GetMapping("/employee/{id}")
    public String getEmployee(@PathVariable int id, Model model){
        model.addAttribute("employees",employeeService.get(id));
        return "employees";
    }

    @ResponseBody
    @DeleteMapping("/employee/delete/{id}")
    public void deleteEmployee(@PathVariable int id){
        employeeService.delete(id);
        System.out.println("Employee Deleted");
    }

    @ResponseBody
    @PutMapping("/employee/update")
    public void updateEmployee(@RequestBody Employee employee, Model model){
        employeeService.save(employee);
        System.out.println("Employee Updated");
    }
}
