package edu.asu.cassess.service.rest;

import edu.asu.cassess.dao.rest.TeamsServiceDao;
import edu.asu.cassess.persist.entity.rest.Team;
import edu.asu.cassess.persist.entity.taiga.Slugs;
import edu.asu.cassess.persist.entity.taiga.TeamNames;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.ejb.EJB;
import java.util.List;

/**
 * Created by Thomas on 3/25/2017.
 */
@Service
public class TeamsService implements ITeamsService {

    @EJB
    private TeamsServiceDao teamsDao;

    @Override
    public <T> Object create(Team team) {
        return teamsDao.create(team);
    }

    @Override
    public <T> Object update(Team team) {
        return teamsDao.update(team);
    }

    @Override
    public <T> Object read(String team_name) {
        return teamsDao.find(team_name);
    }

    @Override
    public <T> Object delete(String team_name) {
        return teamsDao.delete(team_name);
    }

    @Override
    public <T> List<Team> listReadAll() {
        return teamsDao.listReadAll();
    }

    @Override
    public <T> List<Team> listReadByCourse(String course) {
        return teamsDao.listReadByCourse(course);
    }

    @Override
    public JSONObject listCreate(List<Team> teams) {
        return teamsDao.listCreate(teams);
    }

    @Override
    public JSONObject listUpdate(List<Team> teams) {
        return teamsDao.listUpdate(teams);
    }

    @Override
    public List<Slugs> listGetSlugs(String course) {
        return teamsDao.listGetSlugs(course);
    }

    @Override
    public List<TeamNames> listGetTeamNames(String course) {
        return teamsDao.listGetTeamNames(course);
    }

    @Override
    public <T> Object deleteByCourse(String course) {
        return teamsDao.deleteByCourse(course);
    }
}
