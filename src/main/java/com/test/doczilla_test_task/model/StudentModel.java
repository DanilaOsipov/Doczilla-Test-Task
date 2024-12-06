package com.test.doczilla_test_task.model;

import com.fasterxml.jackson.annotation.JsonView;

import java.time.LocalDate;

public class StudentModel {
    @JsonView
    private long id;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate birthDate;
    private String group;

    public StudentModel(long id,
                        String name,
                        String surname,
                        String patronymic,
                        LocalDate birthDate,
                        String group) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.group = group;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
