package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.CanURepository;
import com.canu.repositories.NotificationDetailRepository;
import com.canu.repositories.NotificationRepository;
import com.canu.repositories.TemplateRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
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

    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);

    public void pushNoticeForPostJob(JobModel job) {
        List<CanUModel> canus = canuRepo.findCanIForJobNotification(job.getService(), job.getCreationUser().getId());
        pushNotificationForJob(job, NotificationDetailModel.Type.POST_JOB, canus);
        pushNotificationForJob(job, NotificationDetailModel.Type.CREATE_JOB, Arrays.asList(job.getCreationUser()));
    }

    public void pushNoticeForStartJob(JobModel job) {
        List<CanUModel> canus = Arrays.asList(job.getCreationUser(), job.getRequestedUser());
        pushNotificationForJob(job, NotificationDetailModel.Type.REQUESTED_CANI, canus);
    }

    public void noticeCanUJobComplete(JobModel job) {
        pushNotificationForJob(job, NotificationDetailModel.Type.JOB_COMPLETED, Arrays.asList(job.getCreationUser()));
    }

    private void pushNotificationForJob(JobModel job, NotificationDetailModel.Type notiType, List<CanUModel> canus) {
        try {
            String title = templateRepo.findFirstByType(notiType).getTitle();
            NotificationDetailModel noti = new NotificationDetailModel();
            noti.setType(notiType);
            noti.setData(getNotificationData(noti.getType().toString(), job));
            noti.setTitle(title);
            noti.setDescription(getNotificationData(noti.getType().toTitleString(), job));
            noti = notiRepo.save(noti);
            pushNoticeToUser(canus, noti);
        } catch (Exception ex) {
            logger.error("error on creating notification", ex);
        }
    }

    private String getNotificationData(String type, Object source) {
        try {
            Template template = config.getTemplate(type);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, source);
        } catch (Exception ex) {
            throw new GlobalValidationException("Error on build notification");
        }
    }

    private void pushNoticeToUser(List<CanUModel> users, NotificationDetailModel detail) {
        NotificationModel notice;
        for (CanUModel user : users) {
            notice = new NotificationModel();
            notice.setDetail(detail);
            notice.setOwner(user);
            notice.setTitle(detail.getTitle());
            notice.setTypeNoti(detail.getType());
            notice.setDescription(detail.getDescription());
            notificationRepo.save(notice);
            simpMessagingTemplate.convertAndSend("/api/topic/notification/" + user.getId(), notice);
        }
    }

    public void noticeAdminJobComplete(JobModel job) {
        pushNotificationForAdmin(job, NotificationDetailModel.Type.ADMIN_JOB_COMPLETED);
    }

    public void noticeAdminSupportRequest(SupportRequestModel data) {
        pushNotificationForAdmin(data, NotificationDetailModel.Type.ADMIN_SUPPORT_REQUEST);
    }

    public void noticeAdminCancelJob(JobModel data) {
        pushNotificationForAdmin(data, NotificationDetailModel.Type.CANCEL_JOB_BY_CANU);
    }

    public void noticeAdmin(SupportRequestModel data) {
        pushNotificationForAdmin(data, NotificationDetailModel.Type.ADMIN_SUPPORT_REQUEST);
    }

    private void pushNotificationForAdmin(Object job, NotificationDetailModel.Type notiType) {
        try {
            String title = templateRepo.findFirstByType(notiType).getTitle();
            NotificationDetailModel noti = new NotificationDetailModel();
            noti.setType(notiType);
            noti.setData(getNotificationData(noti.getType().toString(), job));
            noti.setTitle(title);
            noti.setDescription(getNotificationData(noti.getType().toTitleString(), job));
            noti = notiRepo.save(noti);
            pushNoticeToAdmin(noti);
        } catch (Exception ex) {
            logger.error("error on creating notification", ex);
        }
    }

    private void pushNoticeToAdmin(NotificationDetailModel detail) {
        NotificationModel notice = null;
        List<CanUModel> adminList = canuRepo.findByIsAdminIsTrue();
        if(adminList.size() > 0) {
            for (CanUModel user : adminList) {
                notice = new NotificationModel();
                notice.setDetail(detail);
                notice.setOwner(user);
                notice.setTitle(detail.getTitle());
                notice.setTypeNoti(detail.getType());
                notice.setDescription(detail.getDescription());
                notificationRepo.save(notice);
            }
            simpMessagingTemplate.convertAndSend("/api/topic/admin/", notice);
        }
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
        // not throw exception for push notification
        logger.error("error on push notification", ex);
    }

}
