package com.canu.dto.notification;

import com.canu.model.JobModel;
import lombok.Data;

import java.util.Set;

@Data
public class Job {

    public Job(JobModel job){
        this.id = job.getId();
        if(job.getRequestedUser() != null){
            this.owner = job.getRequestedUser().getId();
        }
        this.services = job.getService();
        this.country = job.getNation();
        this.city = job.getCity();

    }
    Long id;

    Long owner;

    Set<Long> services;

    String country;

    String city;
}
