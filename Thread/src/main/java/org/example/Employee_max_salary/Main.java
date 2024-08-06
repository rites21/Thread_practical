package org.example.Employee_max_salary;

import java.util.*;
import java.util.stream.Collectors;

public class  Main {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "Engineering", 70000),
                new Employee("Bob", "Engineering", 80000),
                new Employee("Charlie", "HR", 50000),
                new Employee("David", "HR", 60000),
                new Employee("Eve", "Finance", 75000),
                new Employee("Frank", "Finance", 72000),
                new Employee("Devid", "HR", 72000)
        );

        Map<String, Employee> maxSalaryByDept = employees.stream().
                collect(Collectors.groupingBy(Employee::getDepartment,Collectors.collectingAndThen( Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)),emp->emp.get())));


//        for(Map.Entry<String, Employee> me : maxSalaryByDept.entrySet()){
//            System.out.println(me.getKey()+" "+me.getValue());
//        }

//        maxSalaryByDept.entrySet().forEach(System.out::println);
        List<Employee> sorted = employees.stream().sorted((e1,e2)->e1.getSalary()- e2.getSalary()).limit(3).toList();
        List<Employee> sorted2 = employees.stream().sorted((e1,e2)->e1.getSalary()- e2.getSalary()).skip(3).toList();

        //get max salary
        Optional<Employee> maxSalary = employees.stream().max(Comparator.comparingInt(Employee::getSalary));
        OptionalInt maxSalaryinteger = employees.stream().mapToInt(Employee::getSalary).max();

        //totyal avg salary


        System.out.println(sorted2.toString());
    }
}
