package com.canu.services;

import com.canu.model.JobModel;
import com.canu.repositories.JobRepository;
import com.canu.repositories.MetadataRepository;
import com.canu.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulerService {

    private final ReportRepository reportRepo;

    private final JobRepository jobRepo;

    private final MetadataRepository metaRepo;

    private final SocketService socketSvc;

    @Scheduled(cron = "0 0 1 * * ?")
    void updateAvenue() {
        LocalDate date = LocalDate.now().minusDays(1);
        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        LocalDateTime enđDate = LocalDateTime.of(date, LocalTime.MAX);
        reportRepo.updateAvenue(startDate, enđDate);

    }

    @Scheduled(fixedRate = 120000)
    void notifyPayment() {

        AtomicInteger paymentTime = new AtomicInteger(3600);
        metaRepo.findById("payment_expired").ifPresent(r -> {
            paymentTime.set(Integer.parseInt(r.getValue()));
        });

        LocalDateTime now = LocalDateTime.now().minusMinutes(paymentTime.get());
        LocalDateTime nextTime = now.minusMinutes(2);
        List<JobModel> jobs = jobRepo.findUnToppedUpJob(now, nextTime);
        for(JobModel job : jobs) {
            socketSvc.pushNoticeForPaymentReminder(job);
        }

    }
}
