package com.cassess.service;

import com.cassess.model.github.GitHubCommitDataDao;
import com.cassess.model.taiga.AuthUserQueryDao;
import com.cassess.model.taiga.ProjectQueryDao;
import com.cassess.service.DAO.SlackServiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DataBaseServiceImpl implements DataBaseService {

    @Autowired
    private AuthUserQueryDao authUserQueryDao;

    @Autowired
    private SlackServiceDAO slackServiceDAO;

    @Autowired
    private GitHubCommitDataDao gitHubCommitDataDao;

    @Autowired
    private ProjectQueryDao projectQueryDao;

    @Override
    public String getTaigaToken(){
        return authUserQueryDao.getUser("TaigaTestUser@gmail.com").getAuth_token();
    }

    @Override
    public Long getTaigaID(){
        return  authUserQueryDao.getUser("TaigaTestUser@gmail.com").getId();
    }

    @Override
    public String getSlackTimeZone(){
        return slackServiceDAO.getUser("U2G79FELT").getTz_label();
    }

    @Override
    public String getSlackTeamId(){
        return slackServiceDAO.getUser("U2G79FELT").getTeam_id();
    }

    @Override
    public String getGitHubCommitId(){
        return gitHubCommitDataDao.getCommit("05be0c1b24e9ca67ae8a3a85cb5176c8196c31c7").getCommitID();
    }

    @Override
    public String getGitHubCommitEmail(){
        return gitHubCommitDataDao.getCommit("05be0c1b24e9ca67ae8a3a85cb5176c8196c31c7").getEmail();
    }

    @Override
    public String getProjectCreationDay(){
        return projectQueryDao.getProject("tjjohn1").getCreated_date();
    }

    @Override
    public String getTaigaProjectSlug(){
        return projectQueryDao.getProject("tjjohn1").getSlug();
    }

}
