package edu.asu.cassess.service.rest;

import edu.asu.cassess.model.Taiga.CourseList;
import edu.asu.cassess.model.Taiga.TeamNames;
import edu.asu.cassess.persist.entity.rest.Admin;
import edu.asu.cassess.persist.entity.rest.Student;
import edu.asu.cassess.dao.rest.StudentsServiceDao;
import edu.asu.cassess.persist.entity.rest.Team;
import edu.asu.cassess.persist.entity.security.User;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.ejb.EJB;
import java.util.List;

@Service
public class StudentsService implements IStudentsService {

    @EJB
    private StudentsServiceDao studentsDao;

    @Override
    public <T> Object create(Student student){

        return studentsDao.create(student);
    }

    @Override
    public <T> Object update(Student student){

        return studentsDao.update(student);
    }

    @Override
    public <T> Object find(String email, String team, String course){

        return studentsDao.find(email, team, course);
    }

    @Override
    public <T> Object find(String email, String course) {

        return studentsDao.find(email, course);
    }

    @Override
    public <T> Object delete(Student student){

        return studentsDao.delete(student);

    }

    @Override
    public <T> List<Student> listReadAll(){

        return studentsDao.listReadAll();

    }

    @Override
    public <T> List<Student> listReadByTeam(String course, String team_name) {
        return studentsDao.listReadByTeam(course, team_name);
    }

    @Override
    public JSONObject listUpdate(List<Student> students){

        return studentsDao.listUpdate(students);

    }

    @Override
    public JSONObject listCreate(List<Student> students){

        return studentsDao.listCreate(students);

    }

    @Override
    public <T> Object deleteByTeam(Team team){

        return studentsDao.deleteByTeam(team);

    }

    @Override
    public List<CourseList> listGetCoursesForStudent(String email){
        return studentsDao.listGetCoursesForStudent(email);
    }

    @Override
    public List<TeamNames> listGetAssignedTeams(String email){
        return studentsDao.listGetAssignedTeams(email);
    }

}
