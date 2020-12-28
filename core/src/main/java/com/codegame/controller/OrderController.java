package com.codegame.controller;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.services.OrderService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    final OrderService orderSvc;

    @PostMapping(value = "/create")
    public Object createOrder(@RequestBody CreateOrderRequest request) {
        orderSvc.createOrder(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("received the order request"));
    }
}
