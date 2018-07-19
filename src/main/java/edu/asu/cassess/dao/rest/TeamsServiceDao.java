    package edu.asu.cassess.dao.rest;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.ObjectWriter;
    import edu.asu.cassess.model.Taiga.Slugs;
    import edu.asu.cassess.model.Taiga.TeamNames;
    import edu.asu.cassess.model.github.PeriodicGithubActivity;
    import edu.asu.cassess.persist.entity.rest.*;
    import edu.asu.cassess.persist.repo.rest.TeamRepo;
    import edu.asu.cassess.service.rest.IChannelService;
    import edu.asu.cassess.service.rest.IStudentsService;
    import org.json.JSONArray;
    import org.json.JSONObject;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.dao.DataAccessException;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;

    import javax.persistence.EntityManager;
    import javax.persistence.PersistenceContext;
    import javax.persistence.Query;
    import java.util.List;

    @Component
    public class TeamsServiceDao {

        protected EntityManager entityManager;
        @Autowired
        private TeamRepo teamRepo;
        @Autowired
        private IStudentsService studentsService;
        @Autowired
        private IChannelService channelsService;

        public EntityManager getEntityManager() {
            return entityManager;
        }

        @PersistenceContext
        public void setEntityManager(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Transactional
        public <T> Object create(Team teamInput) {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
            query.setParameter(1, teamInput.getCourse());
            query.setParameter(2, teamInput.getTeam_name());
            List results = query.getResultList();
            if (!results.isEmpty()) {
                return new RestResponse(teamInput.getTeam_name() + " already exists in database");
            } else {
                if (teamInput.getStudents() != null) {
                    studentsService.listCreate(teamInput.getStudents());
                }
                if (teamInput.getChannels() != null) {
                    channelsService.listCreate(teamInput.getChannels());
                }
                teamRepo.save(teamInput);
                return teamInput;
            }
        }

        @Transactional
        public <T> Object update(Team teamInput) {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
            query.setParameter(1, teamInput.getCourse());
            query.setParameter(2, teamInput.getTeam_name());
            List results = query.getResultList();
            if (!results.isEmpty()) {
                Team team = (Team) results.get(0);
                if (team.getStudents() != null) {
                    studentsService.listUpdate(team.getStudents());
                }
                if (team.getChannels() != null) {
                    channelsService.listUpdate(team.getChannels());
                }
                teamRepo.save(teamInput);
                return teamInput;
            } else {
                return new RestResponse(teamInput.getTeam_name() + " does not exist in database");
            }
        }

        @Transactional
        public <T> Object find(String team_name, String course) {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
            query.setParameter(1, course);
            query.setParameter(2, team_name);
            List<Team> resultList = query.getResultList();
            if(!resultList.isEmpty()){
                return (Team) resultList.get(0);
            }else {
                return new RestResponse(team_name + " does not exist in database");
            }
        }

        @Transactional
        public <T> Object delete(Team team) {
            if (team != null) {
                Query query = getEntityManager().createNativeQuery("DELETE FROM teams WHERE course = ?1 AND team_name = ?2", Team.class);
                query.setParameter(1, team.getCourse());
                query.setParameter(2, team.getTeam_name());
                studentsService.deleteByTeam(team);
                channelsService.deleteByTeam(team);
                query.executeUpdate();
                return new RestResponse(team.getTeam_name() + " has been removed from the database");
            } else {
                return new RestResponse(team.getTeam_name() + " does not exist in the database");
            }
        }

        @Transactional
        public List<Team> listReadAll() throws DataAccessException {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams", Team.class);
            List<Team> resultList = query.getResultList();
            return resultList;
        }

        @Transactional
        public List<Team> listReadByCourse(String course) throws DataAccessException {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1", Team.class);
            query.setParameter(1, course);
            List<Team> resultList = query.getResultList();
            return resultList;
        }

        @Transactional
        public List<Slugs> listGetSlugs(String course) throws DataAccessException {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT taiga_project_slug FROM cassess.teams WHERE course = ?1", Slugs.class);
            query.setParameter(1, course);
            List<Slugs> resultList = query.getResultList();
            return resultList;
        }

        @Transactional
        public List<TeamNames> listGetTeamNames(String course) throws DataAccessException {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT team_name AS 'team' FROM cassess.teams WHERE course = ?1", TeamNames.class);
            query.setParameter(1, course);
            List<TeamNames> resultList = query.getResultList();
            return resultList;
        }

        @Transactional
        public <T> Object findOne(String team_name, String course) {
            Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
            query.setParameter(1, course);
            query.setParameter(2, team_name);
            List<Team> results = query.getResultList();
            if (!results.isEmpty()) {
                Team team = (Team) results.get(0);
                return team;
            } else {
                return new RestResponse(team_name + " does not exist for this course");
            }
        }

        @Transactional
        public JSONObject listCreate(List<Team> teams) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            JSONArray successArray = new JSONArray();
            JSONArray failureArray = new JSONArray();
            for (Team teamInput : teams) {
                Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
                query.setParameter(1, teamInput.getCourse());
                query.setParameter(2, teamInput.getTeam_name());
                List<Student> students = teamInput.getStudents();
                List<Channel> channels = teamInput.getChannels();
                List results = query.getResultList();
                if (!results.isEmpty()) {
                    try {
                        failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(teamInput.getTeam_name() + " already exists in database"))));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    teamRepo.save(teamInput);
                    studentsService.listCreate(students);
                    channelsService.listCreate(channels);
                    try {
                        successArray.put(new JSONObject(ow.writeValueAsString(teamInput)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            JSONObject returnJSON = new JSONObject();
            returnJSON.put("success", successArray);
            returnJSON.put("failure", failureArray);
            return returnJSON;
        }

        @Transactional
        public JSONObject listUpdate(List<Team> teams) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            JSONArray successArray = new JSONArray();
            JSONArray failureArray = new JSONArray();
            for (Team teamInput : teams) {
                Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1 AND team_name = ?2", Team.class);
                query.setParameter(1, teamInput.getCourse());
                query.setParameter(2, teamInput.getTeam_name());
                List results = query.getResultList();
                if (results.isEmpty()) {
                    try {
                        failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(teamInput.getTeam_name() + " does not exist in database"))));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    List<Student> students = teamInput.getStudents();
                    teamRepo.save(teamInput);
                    studentsService.listUpdate(students);
                    try {
                        successArray.put(new JSONObject(ow.writeValueAsString(teamInput)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            JSONObject returnJSON = new JSONObject();
            returnJSON.put("success", successArray);
            returnJSON.put("failure", failureArray);
            return returnJSON;
        }

        @Transactional
        public <T> Object deleteByCourse(Course course) {
            Query preQuery = getEntityManager().createNativeQuery("SELECT * FROM cassess.teams WHERE course = ?1 ", Team.class);
            preQuery.setParameter(1, course.getCourse());
            List results = preQuery.getResultList();
            if (!results.isEmpty()) {
                List<Team> teams = results;
                for (Team team : teams) {
                    studentsService.deleteByTeam(team);
                    channelsService.deleteByTeam(team);
                }
                Query query = getEntityManager().createNativeQuery("DELETE FROM cassess.teams WHERE course = ?1");
                query.setParameter(1, course.getCourse());
                query.executeUpdate();
                return new RestResponse("All teams in course " + course.getCourse() + " have been removed from the database");
            } else {
                return new RestResponse("No teams in course " + course.getCourse() + " exist in the database");
            }
        }
        //Retrieve the parameter values for detailed Github data URL
        public PeriodicGithubActivity listGetDetailedGithubActivityURL(String course, String team) throws DataAccessException {
            Query query = getEntityManager().createNativeQuery("select distinct team.github_token,student.github_username,commitdata.github_owner,team.github_repo_id " +
                    "from teams team,students student, commit_data commitdata " +
                    "where team.course = commitdata.course " +
                    "and team.team_name=commitdata.team and commitdata.email=student.email " +
                    "and team.course=?1 and team.team_name=?2");

            query.setParameter(1, course);
            query.setParameter(2, team);
            List<Object[]> results = query.getResultList();
            String userids = "",owner="",repo_id="",token="";
            for(Object[] item : results) {
                token=(String)item[0];
                userids = userids + item[1] + "%2C";
                owner=(String)item[2];
                repo_id=(String)item[3];
            }
            System.out.println("[LOG] "+userids);
            userids = userids.substring(0,userids.length()-3);
            PeriodicGithubActivity finalPOJO = new PeriodicGithubActivity(token,owner,repo_id,userids);
            finalPOJO.setGithub_activity_URL(token,owner,repo_id,userids);
            return finalPOJO;
        }
        public String getAGGithubData(String jsonURL){
            Query query = getEntityManager().createNativeQuery("select ag_result from github_ag where url=?1");
            query.setParameter(1, jsonURL);
            List results = query.getResultList();
            if (!results.isEmpty()) {
                return (String) results.get(0);
            } else {
                return "-1";
            }
        }
        public void updateGithubAG(String jsonURL, String jsonData){
            System.out.println("[LOG]: URL "+jsonURL + " DATA "+jsonData);
            Query query = getEntityManager().createNativeQuery("insert into github_ag select ?1,?2 from dual where not exists (select 1 from github_ag where URL=?1)");
            query.setParameter(1,jsonURL);
            query.setParameter(2,jsonData);
            query.executeUpdate();
            return;
        }
    }