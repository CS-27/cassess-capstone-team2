package com.cassess.service;

import com.cassess.dao.slack.ConsumeUsers;
import com.cassess.entity.slack.UserList;
import com.cassess.entity.slack.UserObject;
import com.cassess.model.github.GatherGitHubData;
import com.cassess.model.taiga.AuthUserQueryDao;
import com.cassess.model.taiga.TaskTotals;
import com.cassess.model.taiga.TaskTotalsQueryDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApiServiceImpl implements ApiService{

    private List<UserObject> users;

    @Autowired
    private ConsumeUsers consumeUsers;

    @Autowired
    private AuthUserQueryDao authUserQueryDao;

    @Autowired
    private GatherGitHubData gatherGitHubData;

    @Autowired
    private TaskTotalsQueryDaoImpl taskTotalsQueryDao;

    @Autowired
    SlackService slackService;

    ApiServiceImpl(){
    }

    @Override
    public List<String> getTeamMembers(){
        List<String> members = new ArrayList<>();
        users = slackService.getAllUsers();
        // Add the names of the members to a list
        for ( UserObject member : users) {
            members.add(member.getName());
        }
        return members;
    }

    @Override
    public List<String> getIds(){
        List<String> Ids = new ArrayList<>();
        for ( UserObject member : users) {
            Ids.add(member.getId());
        }
        return Ids;
    }

    @Override
    public List<String> getUserInfo(){
        List<String> Info = new ArrayList<>();
        Info.add(authUserQueryDao.getUser("taigaTestUser").getFull_name());
        Info.add(authUserQueryDao.getUser("taigaTestUser").getEmail());
        return Info;
    }

    @Override
    public String getGitHubCommitList(){
        return gatherGitHubData.getCommitList().toString();
    }

    @Override
    public String getTaskTotals(){
        List<String> taskTotalsList = new ArrayList<>();
        List<TaskTotals> taskTotalsObj = taskTotalsQueryDao.getTaskTotals();
        for(TaskTotals taskTotals: taskTotalsObj){
            taskTotalsList.add("\n{ Member: " + taskTotals.getId() + ", Name: "
                    + taskTotals.getFull_name() + ", Project: "
                    + taskTotals.getProject_name() + ", Role: "
                    + taskTotals.getRole_name() + ", Retrieval Date: "
                    + taskTotals.getRetrievalDate() + ", Closed Tasks: "
                    + taskTotals.getTasks_closed() + ", New Tasks: "
                    + taskTotals.getTasks_new() + ", In Progress Tasks: "
                    + taskTotals.getTasks_in_progress() + ", Ready For Test Tasks: "
                    + taskTotals.getTasks_ready_for_test() + ", Tasks Open: "
                    + taskTotals.getTasks_open() + " }");
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(taskTotalsList);
        taskTotalsList.clear();
        taskTotalsList.addAll(hs);

        return taskTotalsList.toString();
    }


}
