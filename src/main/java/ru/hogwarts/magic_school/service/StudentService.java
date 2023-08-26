package ru.hogwarts.magic_school.service;

import ru.hogwarts.magic_school.model.Faculty;
import ru.hogwarts.magic_school.model.Student;

import java.util.List;

public interface StudentService {
    Student add(Student student);

    Student get(long id);

    Student update(Student student);

    void remove(long id);

    List<Student> studentsInAge(int age);

    List<Student> getStudentByAgeBetween(int ageMin, int ageMax);

    Faculty getFacultyByStudent(long id);

    int getAllCountStudent();

    int middleAgeByStudents();

    List<Student> getTheLastFiveStudents();


    List<String> getAllNamesWithTheLetterA();

    Integer getAverageAgeOfAllStudents();

    Integer getNumber();

    void threads();

    void threadsSync();
}
