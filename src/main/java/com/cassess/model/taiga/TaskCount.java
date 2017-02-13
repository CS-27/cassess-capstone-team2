package com.cassess.model.taiga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Thomas on 2/11/2017.
 */
@Entity
@Table(name="taskCount")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCount {

    @Id
    @Column(name="Total")
    public int Total;

    public TaskCount(){
    }

    public TaskCount(int Total){
        this.Total = Total;
    }

    public int getTotal() { return Total; }

    public void setTotal(int Total) { this.Total = Total; }

}
