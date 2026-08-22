package example.Employee_max_salary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BinaryOperator;
import java.util.function.Function;
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
                collect(Collectors.toMap(Employee::getDepartment, Function.identity(), BinaryOperator.maxBy(Comparator.comparingInt(Employee::getSalary))));


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


//        System.out.println(sorted2.toString());

        //print all employees
        employees.forEach(employee -> System.out.println(employee.getName() + employee.getSalary()));
        employees.stream().filter(e -> e.getSalary() % 2 == 0).toList();
        //sort employee by asc and desc
//        Distinct words starting with #
        String[] sentences = {"Java is #great and #Java", "I love #Java and #Streams", "#sample #example"};
        List<String> result = Arrays.stream(sentences)
                .flatMap(sentence -> Arrays.stream(sentence.split("\\s+")))
                .filter(word -> word.startsWith("#"))
                .distinct()
                .toList();

        result.forEach(System.out::println);

//        3. First repeating character using Streams

        String fs = "asdfaghjklkjhgfdsa";
        Character res = fs.chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new,
                Collectors.counting()
        )).entrySet().stream().filter(e->e.getValue() >1).map(Map.Entry::getKey).findFirst().orElse(null);
        System.out.println(res);

//        class Student {
//            String name;
//            List<Integer> marks;
//
//            public Student(String rk, int[] ints) {
//            }
//            public int getMarks(){
//                return  0;
//            }
//        }
////        4. Student ranks based on total marks
//        List<Student> students = new ArrayList<>(
//                (Collection) new Student("Rk", new int[]{10, 20, 40})
//        );
//        int highestMarks = students.stream().collect(Collectors.toMap(
//                Function.identity(), student -> student.getMarks().stream(). .mapToInt(Integer::intValue)
//                        .sum()
//        ));
    }
}
