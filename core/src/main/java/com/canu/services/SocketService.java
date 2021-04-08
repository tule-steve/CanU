package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.NotificationDetailModel;
import com.canu.model.NotificationModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.NotificationDetailRepository;
import com.canu.repositories.NotificationRepository;
import com.canu.repositories.TemplateRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SocketService {

    final NotificationDetailRepository notiRepo;

    final SimpMessagingTemplate simpMessagingTemplate;

    final Configuration config;

    final CanURepository canuRepo;

    final TemplateRepository templateRepo;

    final NotificationRepository notificationRepo;

    public void pushNoticeForJob(JobModel job) {
        String title = templateRepo.findFirstByType(NotificationDetailModel.Type.POST_JOB).getTitle();
        NotificationDetailModel noti = new NotificationDetailModel();
        noti.setType(NotificationDetailModel.Type.POST_JOB);
        noti.setData(getNotificationData(noti.getType(), job));
        noti.setTitle(title);
        noti = notiRepo.save(noti);
        List<CanUModel> canus = canuRepo.findCanIByServices(job.getService());
        pushNoticeToUser(canus, noti);
    }

    private String getNotificationData(NotificationDetailModel.Type type, Object source) {
        try {
            String firstTemplate = type.toString();
            Template template = config.getTemplate(firstTemplate);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, source);
        } catch (Exception ex) {
            throw new GlobalValidationException("Error on build notification");
        }
    }

    private void pushNoticeToUser(List<CanUModel> users, NotificationDetailModel detail){
        NotificationModel notice;
        for (CanUModel user : users) {
            notice = new NotificationModel();
            notice.setDetail(detail);
            notice.setOwner(user);
            notice.setTitle(detail.getTitle());
            notice.setTypeNoti(detail.getType());
            notificationRepo.save(notice);
            simpMessagingTemplate.convertAndSend("/api/topic/notification/" + user.getId(), notice);
        }
    }

}
