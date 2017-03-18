package edu.asu.cassess.service.taiga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import edu.asu.cassess.dao.CAssessDAO;
import edu.asu.cassess.dao.taiga.TaskQueryDao;
import edu.asu.cassess.persist.entity.rest.Course;
import edu.asu.cassess.persist.entity.taiga.MemberData;
import edu.asu.cassess.persist.entity.taiga.ProjectIDSlug;
import edu.asu.cassess.persist.entity.taiga.TaskData;
import edu.asu.cassess.persist.entity.taiga.TaskTotals;
import edu.asu.cassess.persist.entity.taiga.TaskTotalsID;
import edu.asu.cassess.service.rest.ICourseService;
import edu.asu.cassess.dao.taiga.MemberQueryDao;
import edu.asu.cassess.dao.taiga.ProjectQueryDao;

import java.util.List;

@Service
@Transactional
public class TaskDataService {

    private RestTemplate restTemplate;

    private String tasksListURL;

    @Autowired
    private CAssessDAO TaskDao;


    @Autowired
    private TaskQueryDao TaskQueryDao;

    @Autowired
    private MemberQueryDao MemberQueryDao;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ProjectQueryDao projectsDao;

    public TaskDataService() {
        restTemplate = new RestTemplate();
        tasksListURL = "https://api.taiga.io/api/v1/tasks?project=";
    }

    public TaskData[] getTasks(Long id, String token, int page) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        headers.add("x-disable-pagination", "True");

        //System.out.println("Page: " + page);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<TaskData[]> taskList = restTemplate.getForEntity(tasksListURL + id + "&page=" + page, TaskData[].class, request);

        TaskData[] tasks = taskList.getBody();

        //System.out.println("Number Results: " + tasks.length);

        for (int i = 0; i < tasks.length - 1; i++) {

            TaskDao.save(tasks[i]);
        }
        //System.out.println("Headers Response" + taskList.getHeaders());

        if (taskList.getHeaders().containsKey("x-pagination-next")) {
            page++;
            return getTasks(id, token, page);
        } else {

            return tasks;
        }
    }

    public void getTaskTotals(String slug) {
        List<MemberData> memberNames = MemberQueryDao.getMembers("Product Owner", slug);
        for (MemberData member: memberNames) {
            String name = member.getFull_name();
            int closedTasks = TaskQueryDao.getClosedTasks(name);
            int newTasks = TaskQueryDao.getNewTasks(name);
            int inProgressTasks = TaskQueryDao.getInProgressTasks(name);
            int readyForTestTasks = TaskQueryDao.getReadyForTestTasks(name);
            int openTasks = newTasks + inProgressTasks + readyForTestTasks;

            TaskDao.save(new TaskTotals(new TaskTotalsID(member.getId()), name, member.getProject_name(), member.getRole_name(), closedTasks, newTasks, inProgressTasks,
                    readyForTestTasks, openTasks));
        }
    }

    /* Method to obtain all task totals for members of a particular course,
        occurring on a schedule
     */
    public void updateTaskTotals(String course){
        System.out.println("Updating Tasks");
        Course tempCourse = (Course) courseService.read(course);
        String token = tempCourse.getTaiga_token();
        List<ProjectIDSlug> idSlugList = projectsDao.listGetProjectIDSlug(course);
        for(ProjectIDSlug idSlug:idSlugList){
            System.out.println("Id: " + idSlug.getId() + "/Slug: " + idSlug.getSlug());
            getTasks(idSlug.getId(), token, 1);
            getTaskTotals(idSlug.getSlug());
        }
    }
}

