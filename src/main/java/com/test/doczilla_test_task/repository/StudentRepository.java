package com.test.doczilla_test_task.repository;

import com.test.doczilla_test_task.model.StudentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class StudentRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void save(String name, String surname, String patronymic, LocalDate birthDate, String group) {
        String sql = "INSERT INTO STUDENTS " +
                "(student_name, student_surname, student_patronymic, student_birth_date, student_group) " +
                "VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, name, surname, patronymic, birthDate, group);
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM STUDENTS WHERE student_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<StudentModel> findAll() {
        String sql = "SELECT * FROM STUDENTS";
        return jdbcTemplate.query(sql, (rs, _) -> getStudentModel(rs));
    }

    private StudentModel getStudentModel(ResultSet rs) throws SQLException {
        return new StudentModel(
                rs.getLong("student_id"),
                rs.getString("student_name"),
                rs.getString("student_surname"),
                rs.getString("student_patronymic"),
                rs.getDate("student_birth_date").toLocalDate(),
                rs.getString("student_group"));
    }
}
