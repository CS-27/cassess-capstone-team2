package edu.asu.cassess.model.github;

import edu.asu.cassess.config.ServiceConfig;

public class PeriodicGithubActivity {
public PeriodicGithubActivity(String github_token, String github_owner, String github_repo_id, String github_username){
	this.github_username = github_username;
	this.github_owner = github_owner;
	this.github_token = github_token;
	this.github_repo_id = github_repo_id;
}
	private String github_token;
	private String github_owner;
	private String github_repo_id;
	private String github_username;
	private String github_activity_URL;

	public String getGithub_token(){
		return this.github_token;
	}
	public String getGithub_owner(){
		return this.github_owner;
	}

	public String getGithub_repo_id(){
		return this.github_repo_id;
	}
	public String getGithub_username(){
		return this.github_username;
	}

	public String getGithub_activity_URL() {
		return this.github_activity_URL;
	}

	public void setGithub_activity_URL(String github_token,String github_owner,String github_repo_id,String github_username) {
		this.github_activity_URL = ServiceConfig.getAG_URL() + "github-stats?token="+github_token
				+"&owner="+github_owner+"&repo="+github_repo_id+"&userids="+github_username;
	}

	public void setGithub_owner(String github_owner) {
		this.github_owner = github_owner;
	}

	public void setGithub_repo_id(String github_repo_id) {
		this.github_repo_id = github_repo_id;
	}

	public void setGithub_token(String github_token) {
		this.github_token = github_token;
	}

	public void setGithub_username(String github_username) {
		this.github_username = github_username;
	}
}
