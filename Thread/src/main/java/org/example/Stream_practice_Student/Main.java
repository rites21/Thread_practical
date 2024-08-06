package org.example.Stream_practice_Student;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Student> list = Arrays.asList(
                new Student(1, "Rohit", "Mall", 30, "Male", "Mechanical Engineering", 2015, "Mumbai", 122),
                new Student(2, "Pulkit", "Singh", 56, "Male", "Computer Engineering", 2018, "Delhi", 67),
                new Student(2, "Aman", "Singh", 56, "Male", "Computer Engineering", 2018, "Delhi", 67),
                new Student(3, "Ankit", "Patil", 25, "Female", "Mechanical Engineering", 2019, "Kerala", 164),
                new Student(4, "Satish Ray", "Malaghan", 30, "Male", "Mechanical Engineering", 2014, "Kerala", 26),
                new Student(5, "Roshan", "Mukd", 23, "Male", "Biotech Engineering", 2022, "Mumbai", 12),
                new Student(6, "Chetan", "Star", 24, "Male", "Mechanical Engineering", 2023, "Karnataka", 90),
                new Student(7, "Arun", "Vittal", 26, "Male", "Electronics Engineering", 2014, "Karnataka", 324),
                new Student(8, "Nam", "Dev", 31, "Male", "Computer Engineering", 2014, "Karnataka", 433),
                new Student(9, "Sonu", "Shankar", 27, "Female", "Computer Engineering", 2018, "Karnataka", 7),
                new Student(10, "Shubham", "Pandey", 26, "Male", "Instrumentation Engineering", 2017, "Mumbai", 98));

//        1- Find list of students whose first name starts with alphabet A
        List<Student> students = list.stream().filter(student -> student.getFirstName().charAt(0)=='A').toList();
//        System.out.println(students);

//        2- Group The Student By Department Names
        Map<String,List<Student>> stringListMap= list.stream().collect(Collectors.groupingBy(Student::getDepartmantName));
//        System.out.println(stringListMap);

//        3- Find the total count of student using stream
        long countOfStudent = list.stream().count();
//        System.out.println();

//        4- Find the max age of student

        OptionalInt maxAge = list.stream().mapToInt(Student::getAge).max();
//        System.out.println(maxAge);

//        5- Find all departments names
        List<String> deptList = list.stream().map(Student::getDepartmantName).toList();
//        System.out.println(deptList);

//        6- Find the count of student in each department
        Map<String, Long> studentCountByDept = list.stream().collect(Collectors.groupingBy(Student::getDepartmantName,Collectors.counting()));

//        7- Find the list of students whose age is less than 30
        List<Student> studentListWithAgeThirty = list.stream().filter(st->st.getAge()<30).toList();

//        8- Find the list of students whose rank is in between 50 and 100
        List<Student> studentsRank = list.stream().filter(st->st.getRank()>=50 && st.getRank()<=100).toList();

//        9- Find the average age of male and female students
        Map<String,Double> mapAvgAge = list.stream().collect(Collectors.groupingBy(Student::getGender,Collectors.averagingInt(Student::getAge)));

//        10- Find the department who is having maximum number of students
//        Map<String,Integer> mapMaxStudent = list.stream().collect(Collectors.groupingBy(st->st.getDepartmantName(),Collectors.maxBy(studentCountByDept)));

//        11- Find the Students who stays in Delhi and sort them by their names
        List<Student> studentListOfDelhi = list.stream().filter(st->st.getCity().equals("Delhi")).sorted((s1,s2)->s1.getFirstName().compareTo(s2.getFirstName())).toList();
//        System.out.println(studentListOfDelhi);

//        12- Find the average rank in all departments
        Map<String,Double> collect = list.stream().collect(Collectors.groupingBy(Student::getDepartmantName,Collectors.averagingInt(Student::getRank)));

//        13- Find the highest rank in each department
            Map<String, Optional<Student>> stringIntegerMap = list.stream().collect(Collectors.groupingBy(Student::getDepartmantName,Collectors.maxBy(Comparator.comparingInt(Student::getRank))));


//        14- Find the list of students and sort them by their rank
        List<Student> sortByRank = list.stream().sorted((s1, s2)->s1.getRank()-s2.getRank()).toList();


//        15- Find the student who has second rank

        Optional<Student> student = Optional.of(list.stream().sorted(Comparator.comparing(Student::getRank)).skip(1).findFirst().get());
    }
}
