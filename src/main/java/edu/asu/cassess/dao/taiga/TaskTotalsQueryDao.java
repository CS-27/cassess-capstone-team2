package edu.asu.cassess.dao.taiga;

import edu.asu.cassess.model.Taiga.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.cassess.persist.entity.taiga.TaskTotals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
@Transactional
public class TaskTotalsQueryDao implements ITaskTotalsQueryDao {

    protected EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TaskTotals> getTaskTotals() throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT * FROM cassess.tasktotals", TaskTotals.class);
        List<TaskTotals> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<DailyTaskTotals> getDailyTasksByProject(String beginDate, String endDate, String course, String project){
        Query query = getEntityManager().createNativeQuery("SELECT retrievalDate as'Date', SUM(tasksInProgress) as 'InProgress', SUM(tasksReadyForTest) as 'ToTest', SUM(tasksClosed) as 'Done' FROM Cassess.tasktotals WHERE retrievalDate >= ?1 AND retrievalDate <= ?2 AND course = ?3 AND project = ?4 GROUP BY retrievalDate", DailyTaskTotals.class);
        query.setParameter(1, beginDate);
        query.setParameter(2, endDate);
        query.setParameter(3, course);
        query.setParameter(4, project);
        List<DailyTaskTotals> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<DailyTaskTotals> getDailyTasksByStudent(String beginDate, String endDate, String course, String project, String student){
        Query query = getEntityManager().createNativeQuery("SELECT retrievalDate as'Date', tasksInProgress as 'InProgress', tasksReadyForTest as 'ToTest', tasksClosed as 'Done' FROM Cassess.tasktotals WHERE retrievalDate >= ?1 AND retrievalDate <= ?2 AND course = ?3 AND project = ?4 AND email = ?5 GROUP BY retrievalDate", DailyTaskTotals.class);
        query.setParameter(1, beginDate);
        query.setParameter(2, endDate);
        query.setParameter(3, course);
        query.setParameter(4, project);
        query.setParameter(5, student);
        List<DailyTaskTotals> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<WeeklyIntervals> getWeeklyIntervalsByStudent(String course, String project, String student){
        Query query = getEntityManager().createNativeQuery("SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding FROM (SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding' FROM cassess.tasktotals WHERE course = ?1 AND project = ?2 AND email = ?3 group by week(retrievalDate)) w1, (select @rn \\:= 0) vars", WeeklyIntervals.class);
                query.setParameter(1, course);
                query.setParameter(2, project);
                query.setParameter(3, student);
                List<WeeklyIntervals> resultList = query.getResultList();
                return resultList;
    }

    @Override
    public List<WeeklyIntervals> getWeeklyIntervalsByProject(String course, String project){
        Query query = getEntityManager().createNativeQuery("SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding FROM (SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding' FROM cassess.tasktotals WHERE course = ?1 AND project = ?2 group by week(retrievalDate)) w1, (select @rn \\:= 0) vars", WeeklyIntervals.class);
                query.setParameter(1, course);
                query.setParameter(2, project);
                List<WeeklyIntervals> resultList = query.getResultList();
                return resultList;
    }

    @Override
    public List<WeeklyUpdateActivity> getWeeklyUpdatesByProject(String course, String project){
        Query query = getEntityManager().createNativeQuery("SELECT TSKF.week, TSKF.weekBeginning, TSKF.weekEnding, \n" +
                "\tTRIM(TRAILING '.' FROM (TRIM(TRAILING '0' FROM (TSKF.DoneActivity - @lastDoneActivity)))) as 'DoneActivity',\n" +
                "      @lastDoneActivity \\:= TSKF.DoneActivity,\n" +
                "\tTSKF.InProgressActivity,\n" +
                "\tTSKF.ToTestActivity\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, SUM(DoneActivity) as 'DoneActivity', SUM(InProgressActivity) as 'InProgressActivity', SUM(ToTestActivity) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', tasksClosed as 'DoneActivity', SUM(tasksInProgressDIFF) as 'InProgressActivity', SUM(tasksReadyForTestDIFF) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      TSK.tasksClosed as 'tasksClosed', \n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress \\:= TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest \\:= TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "    AND project = ?2\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) TSKF, \n" +
                "      ( select @lastDoneActivity \\:= 0) SQLVars\n" +
                "      ", WeeklyUpdateActivity.class);
                query.setParameter(1, course);
                query.setParameter(2, project);
                List<WeeklyUpdateActivity> resultList = query.getResultList();
                return resultList;
    }

    @Override
    public List<WeeklyUpdateActivity> getWeeklyUpdatesByStudent(String course, String project, String student){
        Query query = getEntityManager().createNativeQuery("SELECT TSKF.week, TSKF.weekBeginning, TSKF.weekEnding, \n" +
                "\tTRIM(TRAILING '.' FROM (TRIM(TRAILING '0' FROM (TSKF.DoneActivity - @lastDoneActivity)))) as 'DoneActivity',\n" +
                "      @lastDoneActivity \\:= TSKF.DoneActivity,\n" +
                "\tTSKF.InProgressActivity,\n" +
                "\tTSKF.ToTestActivity\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, SUM(DoneActivity) as 'DoneActivity', SUM(InProgressActivity) as 'InProgressActivity', SUM(ToTestActivity) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', tasksClosed as 'DoneActivity', SUM(tasksInProgressDIFF) as 'InProgressActivity', SUM(tasksReadyForTestDIFF) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      TSK.tasksClosed as 'tasksClosed', \n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress \\:= TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest \\:= TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "    AND project = ?2\n" +
                "    AND email = ?3\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) TSKF, \n" +
                "      ( select @lastDoneActivity \\:= 0) SQLVars\n" +
                "      ", WeeklyUpdateActivity.class);
                query.setParameter(1, course);
                query.setParameter(2, project);
                query.setParameter(3, student);
                List<WeeklyUpdateActivity> resultList = query.getResultList();
                return resultList;
    }

    @Override
    public List<WeeklyUpdateActivity> getWeeklyUpdatesByCourse(String course) {
        Query query = getEntityManager().createNativeQuery("SELECT TSKF.week, TSKF.weekBeginning, TSKF.weekEnding, \n" +
                "\tTRIM(TRAILING '.' FROM (TRIM(TRAILING '0' FROM (TSKF.DoneActivity - @lastDoneActivity)))) as 'DoneActivity',\n" +
                "      @lastDoneActivity \\:= TSKF.DoneActivity,\n" +
                "\tTSKF.InProgressActivity,\n" +
                "\tTSKF.ToTestActivity\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, SUM(DoneActivity) as 'DoneActivity', SUM(InProgressActivity) as 'InProgressActivity', SUM(ToTestActivity) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', tasksClosed as 'DoneActivity', SUM(tasksInProgressDIFF) as 'InProgressActivity', SUM(tasksReadyForTestDIFF) as 'ToTestActivity'\n" +
                "FROM\n" +
                "(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      TSK.tasksClosed as 'tasksClosed', \n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress := TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest := TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) TSKF, \n" +
                "      ( select @lastDoneActivity \\:= 0) SQLVars", WeeklyUpdateActivity.class);
                query.setParameter(1, course);
                List<WeeklyUpdateActivity> resultList = query.getResultList();
                return resultList;
    }

    @Override
    public List<WeeklyWeight> getWeeklyWeightByStudent(String course, String project, String student) {
        Query query = getEntityManager().createNativeQuery("SELECT week, weekBeginning, weekEnding, GREATEST(DoneWeight, InProgressWeight, ToTestWeight) AS weight\n" +
                "FROM\n" +
                "(SELECT week, weekBeginning, weekEnding, \n" +
                "CASE\n" +
                "        WHEN DoneAverage >= 5 THEN 3\n" +
                "        WHEN DoneAverage >= 3 THEN 2\n" +
                "        WHEN DoneAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS DoneWeight,\n" +
                "CASE\n" +
                "        WHEN InProgressAverage >= 5 THEN 3\n" +
                "        WHEN InProgressAverage >= 3 THEN 2\n" +
                "        WHEN InProgressAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS InProgressWeight,\n" +
                "CASE\n" +
                "        WHEN ToTestAverage >= 5 THEN 3\n" +
                "        WHEN ToTestAverage >= 3 THEN 2\n" +
                "        WHEN ToTestAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS ToTestWeight\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, COALESCE(AVG(NULLIF(DoneAverage ,0)), 0) as 'DoneAverage', COALESCE(AVG(NULLIF(InProgressAverage ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(ToTestAverage ,0)), 0) as 'ToTestAverage'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', \n" +
                "DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', COALESCE(AVG(NULLIF(tasksClosedDIFF ,0)), 0) as 'DoneAverage', \n" +
                "COALESCE(AVG(NULLIF(tasksInProgressDIFF ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(tasksReadyForTestDIFF ,0)), 0) as 'ToTestAverage'\n" +
                "FROM(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksClosed - @lasttasksClosed), TSK.tasksClosed) as tasksClosedDIFF,\n" +
                "      @lasttasksClosed \\:= TSK.tasksClosed,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress \\:= TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest \\:= TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "\t\t\t   @lasttasksClosed \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "    AND project = ?2\n" +
                "    AND email = ?3\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) query3\n" +
                "       GROUP BY weekBeginning) query4" +
                "       ORDER BY week DESC LIMIT 2", WeeklyWeight.class);
        query.setParameter(1, course);
        query.setParameter(2, project);
        query.setParameter(3, student);
        List<WeeklyWeight> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<WeeklyWeight> getWeeklyWeightByProject(String course, String project) {
        Query query = getEntityManager().createNativeQuery("SELECT week, weekBeginning, weekEnding, GREATEST(DoneWeight, InProgressWeight, ToTestWeight) AS weight\n" +
                "FROM\n" +
                "(SELECT week, weekBeginning, weekEnding, \n" +
                "CASE\n" +
                "        WHEN DoneAverage >= 5 THEN 3\n" +
                "        WHEN DoneAverage >= 3 THEN 2\n" +
                "        WHEN DoneAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS DoneWeight,\n" +
                "CASE\n" +
                "        WHEN InProgressAverage >= 5 THEN 3\n" +
                "        WHEN InProgressAverage >= 3 THEN 2\n" +
                "        WHEN InProgressAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS InProgressWeight,\n" +
                "CASE\n" +
                "        WHEN ToTestAverage >= 5 THEN 3\n" +
                "        WHEN ToTestAverage >= 3 THEN 2\n" +
                "        WHEN ToTestAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS ToTestWeight\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, COALESCE(AVG(NULLIF(DoneAverage ,0)), 0) as 'DoneAverage', COALESCE(AVG(NULLIF(InProgressAverage ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(ToTestAverage ,0)), 0) as 'ToTestAverage'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', \n" +
                "DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', COALESCE(AVG(NULLIF(tasksClosedDIFF ,0)), 0) as 'DoneAverage', \n" +
                "COALESCE(AVG(NULLIF(tasksInProgressDIFF ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(tasksReadyForTestDIFF ,0)), 0) as 'ToTestAverage'\n" +
                "FROM(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksClosed - @lasttasksClosed), TSK.tasksClosed) as tasksClosedDIFF,\n" +
                "      @lasttasksClosed \\:= TSK.tasksClosed,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress \\:= TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest \\:= TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "\t\t\t   @lasttasksClosed \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "    AND project = ?2\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) query3\n" +
                "       GROUP BY weekBeginning) query4" +
                "       ORDER BY week DESC LIMIT 2", WeeklyWeight.class);
        query.setParameter(1, course);
        query.setParameter(2, project);
        List<WeeklyWeight> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<WeeklyWeight> getWeeklyWeightByCourse(String course) {
        Query query = getEntityManager().createNativeQuery("SELECT week, weekBeginning, weekEnding, GREATEST(DoneWeight, InProgressWeight, ToTestWeight) AS weight\n" +
                "FROM\n" +
                "(SELECT week, weekBeginning, weekEnding, \n" +
                "CASE\n" +
                "        WHEN DoneAverage >= 5 THEN 3\n" +
                "        WHEN DoneAverage >= 3 THEN 2\n" +
                "        WHEN DoneAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS DoneWeight,\n" +
                "CASE\n" +
                "        WHEN InProgressAverage >= 5 THEN 3\n" +
                "        WHEN InProgressAverage >= 3 THEN 2\n" +
                "        WHEN InProgressAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS InProgressWeight,\n" +
                "CASE\n" +
                "        WHEN ToTestAverage >= 5 THEN 3\n" +
                "        WHEN ToTestAverage >= 3 THEN 2\n" +
                "        WHEN ToTestAverage >= 1 THEN 1\n" +
                "        ELSE 0\n" +
                "        END AS ToTestWeight\n" +
                "FROM\n" +
                "(SELECT (@rn \\:= @rn + 1) as 'week', weekBeginning, weekEnding, COALESCE(AVG(NULLIF(DoneAverage ,0)), 0) as 'DoneAverage', COALESCE(AVG(NULLIF(InProgressAverage ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(ToTestAverage ,0)), 0) as 'ToTestAverage'\n" +
                "FROM\n" +
                "(SELECT DATE(retrievalDate + INTERVAL (1 - DAYOFWEEK(retrievalDate)) DAY) as 'weekBeginning', \n" +
                "DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY) as 'weekEnding', COALESCE(AVG(NULLIF(tasksClosedDIFF ,0)), 0) as 'DoneAverage', \n" +
                "COALESCE(AVG(NULLIF(tasksInProgressDIFF ,0)), 0) as 'InProgressAverage', COALESCE(AVG(NULLIF(tasksReadyForTestDIFF ,0)), 0) as 'ToTestAverage'\n" +
                "FROM(select\n" +
                "      TSK.retrievalDate,\n" +
                "      TSK.project,\n" +
                "      TSK.fullName,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksClosed - @lasttasksClosed), TSK.tasksClosed) as tasksClosedDIFF,\n" +
                "      @lasttasksClosed \\:= TSK.tasksClosed,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksInProgress - @lasttasksInProgress), TSK.tasksInProgress) as tasksInProgressDIFF,\n" +
                "      @lasttasksInProgress \\:= TSK.tasksInProgress,\n" +
                "      if( @lastfullName = TSK.fullName, ABS(TSK.tasksReadyForTest - @lasttasksReadyForTest), TSK.tasksReadyForTest ) as tasksReadyForTestDIFF,\n" +
                "      @lastfullName \\:= TSK.fullName,\n" +
                "      @lasttasksReadyForTest \\:= TSK.tasksReadyForTest\n" +
                "   from\n" +
                "      tasktotals TSK,\n" +
                "      ( select @lastfullName \\:= 0,\n" +
                "\t\t\t   @lasttasksClosed \\:= 0,\n" +
                "               @lasttasksInProgress \\:= 0, \n" +
                "               @lasttasksReadyForTest \\:= 0) SQLVars\n" +
                "\tWHERE course = ?1\n" +
                "   order by\n" +
                "      TSK.fullName,\n" +
                "      TSK.retrievalDate) query1\n" +
                "      GROUP BY fullName, DATE(retrievalDate + INTERVAL (7 - DAYOFWEEK(retrievalDate)) DAY)) query2,\n" +
                "      (select @rn \\:= 0) vars\n" +
                "      GROUP BY weekBeginning) query3\n" +
                "       GROUP BY weekBeginning) query4" +
                "       ORDER BY week DESC LIMIT 2", WeeklyWeight.class);
        query.setParameter(1, course);
        List<WeeklyWeight> resultList = query.getResultList();
        return resultList;
    }
}
