package com.canu.dto.requests;

import com.canu.model.JobModel;
import lombok.Value;
import org.springframework.util.StringUtils;

@Value
public class UpdateJobRequest {

    Long id;

    String content;

    String nation;

    Long total;

    String city;

//    JobModel.JobStatus status;

    String currency;

    String title;

//    Long requestedId;

    public void updateJobEntity(JobModel job) {
        if (!StringUtils.isEmpty(content)) {
            job.setContent(this.getContent());
        }

        if (nation != null) {
            job.setNation(nation);
        }

        if (city != null) {
            job.setNation(city);
        }

        if (total != null) {
            job.setTotal(total);
        }

//        if (status != null) {
//            if(JobModel.JobStatus.CANCEL.equals(status)){
//                job.setRequestedUser(null);
//            }
//            job.setStatus(status);
//        }

        if (currency != null) {
            job.setCurrency(currency);
        }

        if(title != null){
            job.setTitle(title);
        }

    }
}
