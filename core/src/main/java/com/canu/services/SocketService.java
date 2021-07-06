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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);

    public void pushNoticeForPostJob(JobModel job) {
        List<CanUModel> canus = canuRepo.findCanIForJobNotification(job.getService(), job.getCreationUser().getId());
        pushNotificationForJob(job, NotificationDetailModel.Type.POST_JOB, canus, false);
        pushNotificationForJob(job,
                               NotificationDetailModel.Type.CREATE_JOB,
                               Arrays.asList(job.getCreationUser()),
                               true);
    }

    public void pushNoticeForPickJob(JobModel job) {
        pushNotificationForJob(job, NotificationDetailModel.Type.PICK_JOB, Arrays.asList(job.getCreationUser()), true);
    }

    public void pushNoticeForStartJob(JobModel job) {
        pushNotificationForJob(job,
                               NotificationDetailModel.Type.ASSIGNED_JOB,
                               Arrays.asList(job.getCreationUser()), true);

        pushNotificationForJob(job,
                               NotificationDetailModel.Type.REQUESTED_CANI,
                               Arrays.asList(job.getRequestedUser()), false);
    }

    public void noticeToppedUpJob(JobModel job) {
        try {
            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.CANU_TOPPED_UP,
                                   Arrays.asList(job.getCreationUser()), true);

            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.TOPPED_UP,
                                   Arrays.asList(job.getRequestedUser()), false);

            noticeAdminCanuTopup(job);
        } catch (Exception ex) {
            logger.error("error on send notification for job with id", job.getId());
        }
    }

    public void noticeCanUSubStatus(JobModel job, SubStatusModel.Status subStatus) {
        pushNotificationForJob(job,
                               NotificationDetailModel.Type.valueOf(subStatus.toString()),
                               Arrays.asList(job.getCreationUser()), true);
    }

    public void noticeCanIJobComplete(JobModel job) {
        pushNotificationForJob(job,
                               NotificationDetailModel.Type.CANI_COMPLETE_JOB,
                               Arrays.asList(job.getCreationUser()), true);
    }

    public void noticeCanUJobComplete(JobModel job) {
        pushNotificationForJob(job,
                               NotificationDetailModel.Type.JOB_COMPLETED,
                               Arrays.asList(job.getCreationUser()),
                               true);
        noticeAdminJobComplete(job);
    }

    public void noticeCanIPaidJob(JobModel job) {
        try {
            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.CANU_PAID_FOR_CANI,
                                   Arrays.asList(job.getCreationUser()), true);
            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.PAID_FOR_CANI,
                                   Arrays.asList(job.getRequestedUser()), false);
        } catch (Exception ex) {
            logger.error("error on send notification for job with id", job.getId());
        }
    }

    private void pushNotificationForJob(JobModel job,
                                        NotificationDetailModel.Type notiType,
                                        List<CanUModel> canus,
                                        boolean isCanU) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("isCanu", isCanU);
            data.put("id", job.getId());
            data.put("title", job.getTitle());
            String title = templateRepo.findFirstByType(notiType).getTitle();
            NotificationDetailModel noti = new NotificationDetailModel();
            noti.setType(notiType);
            noti.setData(getNotificationData(noti.getType().toString(), job));
            noti.setTitle(title);
            noti.setDescription(getNotificationData(noti.getType().toTitleString(), job));
            noti = notiRepo.save(noti);
            pushNoticeToUser(canus, noti, data, isCanU);
        } catch (Exception ex) {
            logger.error("error on creating notification", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void noticeCanIInvalidPaypal(JobModel job) {
        try {
            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.INVALID_PAYPAL_ACCOUNT,
                                   Arrays.asList(job.getCreationUser()), true);

            pushNotificationForJob(job,
                                   NotificationDetailModel.Type.INVALID_PAYPAL_ACCOUNT,
                                   Arrays.asList(job.getRequestedUser()), false);
        } catch (Exception ex) {
            logger.error("error on send notification for job with id", job.getId());
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

    private void pushNoticeToUser(List<CanUModel> users, NotificationDetailModel detail, Object data, boolean isCanU) {
        NotificationModel notice;
        for (CanUModel user : users) {
            notice = new NotificationModel();
            notice.setDetail(detail);
            notice.setOwner(user);
            notice.setTitle(detail.getTitle());
            notice.setTypeNoti(detail.getType());
            notice.setDescription(detail.getDescription());
            notice.setData(data);
            notice.setIsCanu(isCanU);
            notificationRepo.save(notice);
            em.flush();
//            em.clear();
            simpMessagingTemplate.convertAndSend("/api/topic/notification/" + user.getId(), notice);
        }
    }

    public void noticeAdminJobComplete(JobModel job) {
        Map<String, Object> extData = new HashMap<>();
        extData.put("id", job.getId());
        extData.put("title", job.getTitle());

        pushNotificationForAdmin(job, NotificationDetailModel.Type.ADMIN_JOB_COMPLETED, extData);
    }

    public void noticeAdminCanuTopup(JobModel job) {
        Map<String, Object> extData = new HashMap<>();
        extData.put("id", job.getId());
        extData.put("title", job.getTitle());

        pushNotificationForAdmin(job, NotificationDetailModel.Type.ADMIN_CANU_TOPPED_UP, extData);
    }

    public void noticeAdminSupportRequest(SupportRequestModel data) {
        pushNotificationForAdmin(data, NotificationDetailModel.Type.ADMIN_SUPPORT_REQUEST, null);
    }

    public void noticeAdminCancelJob(JobModel data) {
        pushNotificationForAdmin(data, NotificationDetailModel.Type.CANCEL_JOB_BY_CANU, null);
    }

    private void pushNotificationForAdmin(Object job, NotificationDetailModel.Type notiType, Object extData) {
        try {
            String title = templateRepo.findFirstByType(notiType).getTitle();
            NotificationDetailModel noti = new NotificationDetailModel();
            noti.setType(notiType);
            noti.setData(getNotificationData(noti.getType().toString(), job));
            noti.setTitle(title);
            noti.setDescription(getNotificationData(noti.getType().toTitleString(), job));
            noti = notiRepo.save(noti);
            pushNoticeToAdmin(noti, extData);
        } catch (Exception ex) {
            logger.error("error on creating notification", ex);
        }
    }

    private void pushNoticeToAdmin(NotificationDetailModel detail, Object extData) {
        NotificationModel notice = new NotificationModel();
        notice.setDetail(detail);
        notice.setOwner(null);
        notice.setTitle(detail.getTitle());
        notice.setTypeNoti(detail.getType());
        notice.setDescription(detail.getDescription());
        notice.setData(extData);
        notice.setIsAdmin(true);
        notificationRepo.save(notice);
        simpMessagingTemplate.convertAndSend("/api/topic/admin/", notice);
    }

    //    @ExceptionHandler(Exception.class)
    //    public void handleException(Exception ex) {
    //        // not throw exception for push notification
    //        logger.error("error on push notification", ex);
    //    }

}
