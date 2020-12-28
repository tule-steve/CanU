package com.canu.controller;

import com.canu.dto.requests.TransactionRequest;
import com.canu.services.PaymentService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/payment")
public class PaymentController {

    final private PaymentService paymentSvc;

    @PostMapping(value = "/get-transaction-info")
    public ResponseEntity addCoupon(@RequestBody @Validated TransactionRequest request){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", paymentSvc.getTransactionDetail(request)));
    }



}
