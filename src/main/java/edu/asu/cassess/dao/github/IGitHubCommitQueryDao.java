package edu.asu.cassess.dao.github;

import edu.asu.cassess.model.Taiga.WeeklyFreqWeight;
import edu.asu.cassess.persist.entity.github.CommitData;
import edu.asu.cassess.persist.entity.rest.RestResponse;
import edu.asu.cassess.persist.entity.rest.Student;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Thomas on 4/19/2017.
 */
public interface IGitHubCommitQueryDao {
    EntityManager getEntityManager();

    @PersistenceContext
    void setEntityManager(EntityManager entityManager);

    @Transactional
    RestResponse deleteCommitsByStudent(Student student) throws DataAccessException;

    @Transactional
    List<WeeklyFreqWeight> getWeightFreqByCourse(String course) throws DataAccessException;

    @Transactional
    List<WeeklyFreqWeight> getWeightFreqByTeam(String course, String team) throws DataAccessException;

    @Transactional
    List<WeeklyFreqWeight> getWeightFreqByStudent(String course, String team, String email) throws DataAccessException;

    @Transactional
    List<CommitData> getCommitsByCourse(String course) throws DataAccessException;

    @Transactional
    List<CommitData> getCommitsByCourseWithinDate(String course, String beginDate, String endDate) throws DataAccessException;

    @Transactional
    List<CommitData> getCommitsByTeam(String course, String team) throws DataAccessException;

    @Transactional
    List<CommitData> getCommitsByStudent(String course, String team, String email) throws DataAccessException;
}
