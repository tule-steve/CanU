package com.canu.controller;

import com.canu.dto.requests.TransactionRequest;
import com.canu.services.PaymentService;
import com.canu.specifications.PaymentFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/payment")
public class PaymentController {

    final private PaymentService paymentSvc;

    @PostMapping(value = "/get-transaction-info")
    public ResponseEntity getCart(@RequestBody @Validated TransactionRequest request){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", paymentSvc.getTransactionDetail(request)));
    }

    @GetMapping(value = "/history")
    public ResponseEntity getPaymentHistory(PaymentFilter filter, Pageable p){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", paymentSvc.getPaymentList(filter, p)));
    }

    @GetMapping(value = "test-payout/{jobId}")
    public ResponseEntity testPayout(@PathVariable Long jobId){
        paymentSvc.payout(jobId);
        return ResponseEntity.ok("ok");
    }



}
