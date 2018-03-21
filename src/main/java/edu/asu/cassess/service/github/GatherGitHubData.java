package edu.asu.cassess.service.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.cassess.dao.github.IGitHubCommitDataDao;
import edu.asu.cassess.dao.github.IGitHubWeightQueryDao;
import edu.asu.cassess.model.github.GitHubAnalytics;
import edu.asu.cassess.persist.entity.github.CommitData;

import edu.asu.cassess.persist.entity.github.GitHubWeight;
import edu.asu.cassess.persist.entity.rest.Course;
import edu.asu.cassess.persist.entity.rest.Student;
import edu.asu.cassess.persist.repo.github.CommitDataRepo;
import edu.asu.cassess.persist.repo.github.GitHubWeightRepo;
import edu.asu.cassess.service.rest.CourseService;
import edu.asu.cassess.service.rest.ICourseService;
import edu.asu.cassess.service.rest.IStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class GatherGitHubData implements IGatherGitHubData {

    @Autowired
    private IGitHubCommitDataDao commitDao;

    @Autowired
    private GitHubWeightRepo weightRepo;

    @Autowired
    private CommitDataRepo commitDataRepo;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IGitHubWeightQueryDao ghWeightQuery;

    private RestTemplate restTemplate;
    private String url;
    private String projectName;
    private String owner;

    public GatherGitHubData() {
        restTemplate = new RestTemplate();
    }

    /**
     * Gathers the GitHub commit data from the GitHub Repo Stats
     * The github owner and project name can be found in the repo url as follows
     * www.github.com/:owner/:projectName
     *
     * @param owner       the owner of the repo
     * @param projectName the project name of the repo
     */
    @Override
    public void fetchData(String owner, String projectName, String course, String team, String accessToken) {
        if (courseService == null) courseService = new CourseService();
        Course tempCourse = (Course) courseService.read(course);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        String formatted = df.format(new java.util.Date());
        java.util.Date current = new java.util.Date();
        try {
            current = df.parse(formatted);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println("Getting Stats for course: " + course + " & Team: " + team);
        if (current.before(tempCourse.getEnd_date())) {
            this.projectName = projectName;
            this.owner = owner;
            url = "https://api.github.com/repos/" + owner + "/" + projectName + "/";
            getStats(course, team, accessToken);
        } else {
            //System.out.println("*****************************************************Course Ended, no GH data Gathering");
        }
    }

    private void getStats(String course, String team, String accessToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + "stats/contributors")
                .queryParam("access_token", accessToken + "&scope=&token_type=bearer");
        String urlPath = builder.build().toUriString();

        //System.out.println("GitHub URL: " + urlPath);

        String json = restTemplate.getForObject(urlPath, String.class);

        List<GitHubContributors> contributors = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            contributors = mapper.readValue(json, new TypeReference<List<GitHubContributors>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        storeStats(contributors, accessToken, course, team);
    }

    private void storeStats(List<GitHubContributors> contributors, String accessToken, String course, String team) {
        //System.out.println("Storing Stats for course: " + course + " & Team: " + team);
        Collections.reverse(contributors);

        for (GitHubContributors contributor : contributors) {
            //System.out.println("*******************************************************Iterating Contributors");
            Weeks[] weeks = contributor.getWeeks();
            //System.out.println("*******************************************************Weeks: " + weeks);

            String userName = contributor.getAuthor().getLogin();

            //System.out.println("*******************************************************Username: " + userName);

            Date lastDate = null;

            if (ghWeightQuery.getlastDate(course, team, userName) != null) {
                lastDate = ghWeightQuery.getlastDate(course, team, userName).getGitHubPK().getDate();
            }
            //System.out.println("*******************************************************LastDate: " + lastDate);
            //System.out.println("*******************************************************UserName: " + userName);

            boolean ghMatch = false;
            boolean studentEnabled = false;


            Student student = new Student();
            //System.out.println("Finding Student for Course: " + course + " & Team: " + team + " & Email: " + email);
            Object object = studentsService.findGitHubUser(userName, team, course);
            if (object.getClass() == Student.class) {
                student = (Student) object;
                ghMatch = true;
                if (student.getEnabled() != null) {
                    if (student.getEnabled()) {
                        //System.out.println("**********************************************Enabled");
                        studentEnabled = true;
                    } else {
                        //System.out.println("**********************************************Not Enabled");
                        studentEnabled = false;
                    }
                } else {
                    //System.out.println("**********************************************Not Enabled");
                    studentEnabled = false;
                }
            } else {
                //System.out.println("**********************************************Not a Student);
                ghMatch = false;
            }
            //System.out.println("Found Student for Course: " + student.getCourse() + " & Team: " + student.getTeam_name() + " & Email: " + student.getEmail());


            if (studentEnabled && ghMatch) {
                for (Weeks week : weeks) {
                    Date date = new Date(week.getW() * 1000L);
                    //System.out.print("*********************************************************ContribDate: " + date);
                    if (lastDate == null || !date.before(lastDate)) {
                        //System.out.println("*****************************************************Inserting to DB");
                        int linesAdded = week.getA();
                        int linesDeleted = week.getD();
                        int commits = week.getC();

                        //System.out.println("Saving Commit Data for Course: " + course + " & Team: " + team + " & Email: " + email);
                        commitDataRepo.save(new CommitData(date, userName, student.getEmail(), linesAdded, linesDeleted, commits, projectName, owner, team, course));

                        int weight = GitHubAnalytics.calculateWeight(linesAdded, linesDeleted);
                        //System.out.println("Saving Weight Data for Course: " + course + " & Team: " + team + " & Email: " + email);
                        GitHubWeight gitHubWeight = new GitHubWeight(student.getEmail(), date, weight, userName, team, course);
                        weightRepo.save(gitHubWeight);
                    } else {
                        //System.out.println("*****************************************************Not Inserting to DB");
                    }
                }
            } else {
                //System.out.println("*****************************************************Not a match or not enabled");
            }
        }
    }

    /**
     * Gathers all the rows in the commit_data table
     *
     * @return A list of CommitData Objects
     */
    @Override
    public List<CommitData> getCommitList(){
        return commitDao.getAllCommitData();
    }
}