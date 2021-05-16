package com.canu.services;

import com.canu.dto.requests.CompletePaymentRequest;
import com.canu.dto.requests.TransactionRequest;
import com.canu.dto.responses.TransactionDetailDto;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.specifications.PaymentFilter;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnit;
import com.paypal.payouts.*;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    final private JobRepository jobRepo;

    final private CanURepository canURepo;

    final private UserCouponRepository userCouponRepo;

    final private PaymentRepository paymentRepo;

    final private ChatService chatSvc;

    final private SocketService socketSvc;

    final private EntityManager em;

    final private PropertyRepository propertyRepo;

    private static final Logger logger = Logger.getLogger(PaymentService.class);

    private static String EMAIL_PAYMENT_TYPE = "EMAIL";

    private static String PHONE_PAYMENT_TYPE = "PHONE";

    // Creating a sandbox environment
    private static PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
            "AZ5zj4wGJv_JEdqcmd2f0i1Mie-ug5Ru6i71mumlEuXFKZ9vtw1bR8B5OJqq_-xgwk8SSUjZxl6RJBd-",
            "EIo0zOxLoQvu44afwwDJ9EFUhtHWq2vhFXBcn0Gv5wE1MliZK4eZ3aj2weQ6tRRiLxLBfwrcEnQ0kqiF");

    // Creating a client for the environment
    static PayPalHttpClient client = new PayPalHttpClient(environment);

    public Object getTransactionDetail(TransactionRequest request) {
        JobModel job = jobRepo.findById(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException(
                                      "Cannot find the job with id: " + request.getJobId()));

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        PaymentModel payment;
        if (job.getPayments().size() == 0) {
            payment = new PaymentModel();
            payment.setJob(job);
            payment.setOwner(uUser);
        } else {
            payment = job.getPayments().get(0);
        }
        //
        payment.setTotal(BigDecimal.valueOf(job.getTotal()));
        payment.setCurrency(job.getCurrency());
        if (request.getCouponCode() != null) {
            UserCouponModel userCoupon = userCouponRepo.getTransactionVoucher(uUser, request.getCouponCode(),
                                                                              CouponModel.Status.AVAILABLE,
                                                                              LocalDateTime.now())
                                                       .orElseThrow(() -> new GlobalValidationException(
                                                               "cannot find the coupon"));

            payment.setUserCoupon(userCoupon);
        } else {
            payment.setUserCoupon(null);
        }
        PropertyModel pointExchangeRate = propertyRepo.findFirstByTypeAndKey(PropertyModel.Type.POINT_EXCHANGE,
                                                                             job.getCurrency());
        TransactionDetailDto response = new TransactionDetailDto(payment, job, em, pointExchangeRate);
        paymentRepo.save(payment);

        return response;
    }

    public void complete(CompletePaymentRequest request) {
        Order paymentOrder = captureOrder(request.getPaypalOrderId());
        if ("COMPLETED".equalsIgnoreCase(paymentOrder.status())) {
            Long paymentId = Long.parseLong(paymentOrder.purchaseUnits().get(0).customId());
            PaymentModel paymentModel = paymentRepo.findById(paymentId).orElseThrow(() -> {
                //TODO should notify someone
                logger.error(paymentOrder);
                return new GlobalValidationException("cannot find the customer id in response");
            });
            PurchaseUnit purchaseUnit = paymentOrder.purchaseUnits().get(0);
            //validate amount

            JobModel job = paymentModel.getJob();
            if (paymentModel.getTotal().compareTo(new BigDecimal(purchaseUnit.amountWithBreakdown().value())) == 0 &&
                paymentModel.getCurrency().equalsIgnoreCase(purchaseUnit.amountWithBreakdown().currencyCode())) {
                paymentModel.setStatus(PaymentModel.Status.TOPPED_UP);
                paymentModel.setTransactionId(paymentOrder.id());
                paymentModel.setPaymentMethod("paypal");
                if (paymentModel.getJob() != null) {
                    chatSvc.sendPaymentCompleteMessage(job);
                }
                paymentRepo.save(paymentModel);
                job.setStatus(JobModel.JobStatus.PROCESSING);
                jobRepo.save(job);
                if (paymentModel.getUserCoupon() != null) {
                    paymentModel.getUserCoupon().setStatus(CouponModel.Status.REDEEMED);
                    userCouponRepo.save(paymentModel.getUserCoupon());
                }
                socketSvc.noticeToppedUpJob(paymentModel.getJob());

            }
        } else {
            logger.error(paymentOrder);
            throw new GlobalValidationException("payment not success");
        }

    }

    private Order captureOrder(String orderId) {
        Order order;
        OrdersGetRequest request = new OrdersGetRequest(orderId);

        try {
            // Call API with your client and get a response for your call
            HttpResponse<Order> response = client.execute(request);

            // If call returns body in response, you can get the de-serialized version by
            // calling result() on the response
            order = response.result();
            logger.info("order payment response for order: " + orderId);
            logger.info(NotificationDetailModel.mapper.writeValueAsString(order));
            return order;
        } catch (IOException ioe) {
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                logger.error("error on the check payment", ioe);
            } else {
                // Something went wrong client-side
                logger.error("error on the check payment, client side", ioe);
            }
            throw new GlobalValidationException("cannot get the payment detail from paypal");
        }

    }

    public void payout(Long jobId) {
        JobModel job = jobRepo.findById(jobId).orElseThrow(() -> new GlobalValidationException("cannot find the job"));
        payout(job);
    }

    public void payout(JobModel job) {
        long jobId = job.getId();
        try {
            CanIModel cani = job.getRequestedUser().getCanIModel();
            String receiver;
            String paymentType;
            Long total = job.getTotal();
            String transactionId = "job_id_" + jobId + LocalDateTime.now();

            if (!StringUtils.isEmpty(cani.getPaypalEmail())) {
                receiver = cani.getPaypalEmail();
                paymentType = EMAIL_PAYMENT_TYPE;
            } else if (!StringUtils.isEmpty(cani.getPaypalPhone())) {
                receiver = cani.getPaypalPhone();
                paymentType = PHONE_PAYMENT_TYPE;
            } else {
                logger.error("paypal not set up");
                socketSvc.noticeCanIInvalidPaypal(job);
                throw new GlobalValidationException("cannot get the payment detail from paypal");
            }
            CreatePayoutRequest request = new CreatePayoutRequest();
            PayoutItem item = new PayoutItem()
                    .senderItemId(transactionId)
                    .note("payout for job " + job.getTitle())
                    .receiver(receiver)
                    .amount((new Currency()
                            .currency(job.getCurrency())
                            .value(total.toString())));

            request.senderBatchHeader(new SenderBatchHeader()
                                              .senderBatchId(transactionId)
                                              .emailMessage("payout for job " + job.getTitle())
                                              .emailSubject("payout for job " + job.getTitle())
                                              .note("Enjoy your Payout!!")
                                              .recipientType(paymentType)).items(Arrays.asList(item));

            PayoutsPostRequest httpRequest = new PayoutsPostRequest().requestBody(request);
            HttpResponse<CreatePayoutResponse> response = client.execute(httpRequest);

            CreatePayoutResponse payouts = response.result();

            PaymentModel payment = new PaymentModel();
            payment.setJob(job);
            payment.setOwner(job.getRequestedUser());
            payment.setStatus(PaymentModel.Status.PAID);
            payment.setPaymentMethod("paypal");
            payment.setTotal(BigDecimal.valueOf(total));
            payment.setTransactionId(payouts.batchHeader().payoutBatchId());
            payment.setCurrency(job.getCurrency());
            paymentRepo.save(payment);
            socketSvc.noticeCanIPaidJob(job);

            //            PayoutsGetRequest getRequest = new PayoutsGetRequest(payouts.batchHeader().payoutBatchId());
            //            HttpResponse response1 = client.execute(getRequest);
            //            logger.error("rrerel");
            //            int a = 0;
            //            a++;

            //            System.out.println("Payouts Batch ID: " + payouts.batchHeader().payoutBatchId());
        } catch (IOException ioe) {
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                logger.error("error on the check payment", ioe);
            } else {
                // Something went wrong client-side
                logger.error("error on the check payment, client side", ioe);
            }
            socketSvc.noticeCanIInvalidPaypal(job);
            throw new GlobalValidationException("cannot get the payment detail from paypal");
        }

    }

    @Transactional(readOnly = true)
    public Page<TransactionDetailDto> getPaymentList(PaymentFilter filter, Pageable p) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        filter.setUserId(uUser);
        Page<PaymentModel> payments = paymentRepo.findAll(filter, p);
        TransactionDetailDto detail;
        List<TransactionDetailDto> dtoResult = new ArrayList<>();
        for (PaymentModel payment : payments) {
            PropertyModel pointExchangeRate = propertyRepo.findFirstByTypeAndKey(PropertyModel.Type.POINT_EXCHANGE,
                                                                                 payment.getJob().getCurrency());

            detail = new TransactionDetailDto(payment, payment.getJob(), em, pointExchangeRate);
            dtoResult.add(detail);
        }

        Page<TransactionDetailDto> result = new PageImpl<>(dtoResult, p, payments.getTotalElements());
        return result;
    }
}
