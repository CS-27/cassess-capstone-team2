package edu.asu.cassess.web.controller;

import edu.asu.cassess.dao.taiga.ITaskTotalsQueryDao;
import edu.asu.cassess.persist.entity.rest.*;
import edu.asu.cassess.service.rest.*;

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
    private CourseService courseService;

    @Autowired
    private TeamsService teamService;

    @Autowired
    private StudentsService studentService;

    @Autowired
    private AdminsService adminService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    ITaskTotalsQueryDao taskTotalService;


//-----------------------

    //New CoursePackage REST API Operations
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


    //New Student REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteStudent(@RequestBody Student student, HttpServletRequest request, HttpServletResponse response) {
        if(student != null){
            response.setStatus(HttpServletResponse.SC_OK);
            return studentService.delete(student.getEmail());
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
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
            return studentService.update(student);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/student", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> List<Student> getStudentList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);
            return studentService.listReadAll();
        }

    //New Course REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteCourse(@RequestBody Course course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return courseService.delete(course.getCourse());
        }
    }

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
    @RequestMapping(value = "/list/course", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readCourseList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);

            return courseService.listRead();
        }

    //New Slack Channel REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/slack_channel", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteChannel(@RequestBody Channel channel, HttpServletRequest request, HttpServletResponse response) {
        if (channel == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return channelService.delete(channel.getCourse());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/slack_channel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateChannel(@RequestBody Channel channel, HttpServletRequest request, HttpServletResponse response) {
        if (channel == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return channelService.update(channel);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/slack_channel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateChannelList(@RequestBody List<Channel> channels, HttpServletRequest request, HttpServletResponse response) {
        if (channels == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return channelService.listUpdate(channels);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/slack_channel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readChannelList(HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_OK);
            return channelService.listRead();
        }

    //New Admin REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/admin", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteAdmin(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response) {
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return adminService.delete(admin.getEmail());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/admin", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateAdmin(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response) {
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return adminService.update(admin);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/admin", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateAdmin(@RequestBody List<Admin> admins, HttpServletRequest request, HttpServletResponse response) {
        if (admins == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return adminService.listUpdate(admins);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readAdminList(HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_OK);
            return adminService.listReadAll();
        }


    //New Team REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/team", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteTeam(@RequestBody Team team, HttpServletRequest request, HttpServletResponse response) {

        if (team == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return teamService.delete(team.getTeam_name());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/team", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateTeam(@RequestBody Team team, HttpServletRequest request, HttpServletResponse response) {

        if (team == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return teamService.update(team);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/team", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateTeamList(@RequestBody List<Team> teams, HttpServletRequest request, HttpServletResponse response) {

        if (teams == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return teamService.listUpdate(teams);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/team", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readTeamList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);

            return teamService.listReadAll();
        }


}

