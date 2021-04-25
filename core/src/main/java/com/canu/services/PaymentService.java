package com.canu.services;

import com.canu.dto.requests.CompletePaymentRequest;
import com.canu.dto.requests.TransactionRequest;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.CanURepository;
import com.canu.repositories.JobRepository;
import com.canu.repositories.PaymentRepository;
import com.canu.repositories.UserCouponRepository;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnit;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    final private JobRepository jobRepo;

    final private CanURepository canURepo;

    final private UserCouponRepository userCouponRepo;

    final private PaymentRepository paymentRepo;

    final private ChatService chatSvc;

    private static final Logger logger = Logger.getLogger(PaymentService.class);

    // Creating a sandbox environment
    private static PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
            "AZ5zj4wGJv_JEdqcmd2f0i1Mie-ug5Ru6i71mumlEuXFKZ9vtw1bR8B5OJqq_-xgwk8SSUjZxl6RJBd-",
            "EIo0zOxLoQvu44afwwDJ9EFUhtHWq2vhFXBcn0Gv5wE1MliZK4eZ3aj2weQ6tRRiLxLBfwrcEnQ0kqiF");

    // Creating a client for the environment
    static PayPalHttpClient client = new PayPalHttpClient(environment);

    public PaymentModel getTransactionDetail(TransactionRequest request) {
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

        payment.setTotal(BigDecimal.valueOf(job.getTotal()));
        payment.setCurrency(job.getCurrency());
        if (request.getCouponCode() != null) {
            UserCouponModel userCoupon = userCouponRepo.getTransactionVoucher(uUser, request.getCouponCode(),
                                                                                           CouponModel.Status.AVAILABLE)
                                                       .orElseThrow(() -> new GlobalValidationException(
                                                               "cannot find the coupon"));

            payment.setUserCoupon(userCoupon);
            payment.setTotal(payment.getTotal().subtract(userCoupon.getCoupon().getValue()));
        }
        paymentRepo.save(payment);
        return payment;
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

            if (paymentModel.getTotal().compareTo(new BigDecimal(purchaseUnit.amountWithBreakdown().value())) == 0 &&
                paymentModel.getCurrency().equalsIgnoreCase(purchaseUnit.amountWithBreakdown().currencyCode())) {
                paymentModel.setStatus(PaymentModel.Status.TOPPED_UP);
                paymentModel.setTransactionId(paymentOrder.id());
                paymentModel.setPaymentMethod("paypal");
                if(paymentModel.getJob() != null) {
                    chatSvc.sendPaymentCompleteMessage(paymentModel.getJob());
                }
                paymentRepo.save(paymentModel);
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
}
