package edu.asu.cassess.service.rest;

import java.util.List;

import edu.asu.cassess.model.Taiga.CourseList;
import edu.asu.cassess.model.Taiga.TeamNames;
import edu.asu.cassess.persist.entity.rest.Team;
import edu.asu.cassess.persist.entity.security.User;
import org.json.JSONObject;

import edu.asu.cassess.persist.entity.rest.Student;

public interface IStudentsService {

    <T> Object create(Student student);

    <T> Object update(Student student);

    <T> Object find(String email, String team, String course);

    <T> Object delete(Student student);

    <T> List<Student> listReadAll();

    <T> List<Student> listReadByTeam(String team_name);

    JSONObject listUpdate(List<Student> students);

    JSONObject listCreate(List<Student> students);

    <T> Object deleteByTeam(Team team);

    List<CourseList> listGetCoursesForStudent(String email);

    List<TeamNames> listGetAssignedTeams(String email);
}