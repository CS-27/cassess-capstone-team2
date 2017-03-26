package edu.asu.cassess.web.controller;

import edu.asu.cassess.dao.taiga.ITaskTotalsQueryDao;
import edu.asu.cassess.persist.entity.rest.Course;
import edu.asu.cassess.persist.entity.rest.Student;
import edu.asu.cassess.service.rest.ICourseService;
import edu.asu.cassess.service.rest.IStudentsService;

import edu.asu.cassess.service.rest.ITeamsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiringInspection")
@RestController
@RequestMapping(value = "/rest")
@Api(description = "Nicest Provisioning API")
public class restController {


    @Autowired
    private ICourseService courseService;

    @Autowired
    private ITeamsService teamService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    ITaskTotalsQueryDao taskTotalService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateCourse(@RequestBody Course course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.update(course);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object newCourse(@RequestBody Course course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.create(course);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", params = "course", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object getCourse(@RequestParam("course") String course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.read(course);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", params = "course", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteCourse(@RequestParam("course") String course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            //System.out.println("Tool: " + tool);
            return courseService.delete(course);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course_list", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String updateCourses(@RequestBody ArrayList<Course> courses, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (courses == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
                return courseService.listUpdate(courses).toString();
        }

    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course_list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String newCourses(@RequestBody ArrayList<Course> courses, HttpServletRequest request, HttpServletResponse response) {

        if (courses == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.listCreate(courses).toString();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> List<Course> getAllCourses(HttpServletRequest request, HttpServletResponse response) {

        response.setStatus(HttpServletResponse.SC_OK);
        return courseService.listRead();
    }


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateStudent(@RequestBody Student student, HttpServletRequest request, HttpServletResponse response) {

        if (student == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.update(student);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object newStudent(@RequestBody Student student, HttpServletRequest request, HttpServletResponse response) {

        if (student == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.create(student);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object getStudent(@RequestParam(value = "email", required = true) String email, HttpServletRequest request, HttpServletResponse response) {

        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.find(email);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student_list", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String updateStudentList(@RequestBody List<Student> students, HttpServletRequest request, HttpServletResponse response) {

        if (students == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.listUpdate(students).toString();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student_list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String newStudentList(@RequestBody List<Student> students, HttpServletRequest request, HttpServletResponse response) {

        if (students == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.listCreate(students).toString();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> List<Student> getStudents(@RequestParam(value = "team_name", required = false) String team_name, HttpServletRequest request, HttpServletResponse response) {

        if(team_name != null){
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.listReadByTeam(team_name);
        }else{
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.listReadAll();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object delete(@RequestParam(value = "team_name", required = false) String team_name, @RequestParam(value = "email", required = false) String email,
                                       HttpServletRequest request, HttpServletResponse response) {
        if(team_name != null){
            System.out.println("Team: " + team_name);
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.deleteByTeam(team_name);
        }else if(email != null){
            System.out.println("Email: " + email);
            response.setStatus(HttpServletResponse.SC_OK);
            return studentsService.delete(email);
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coursePackage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object newCoursePackage(@RequestBody Course coursePackage, HttpServletRequest request, HttpServletResponse response) {

        if (coursePackage == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.create(coursePackage);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coursePackage", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateCoursePackage(@RequestBody Course coursePackage, HttpServletRequest request, HttpServletResponse response) {

        if (coursePackage == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return courseService.update(coursePackage);
        }
    }

}

