package com.codegame.controller;

import com.codegame.dto.AddGiftCardRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.model.Item;
import com.codegame.services.AdminService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    final AdminService adminSvc;

    @PostMapping(value = "/refund")
    public ResponseEntity refund(@RequestBody RefundRequest request) {
        adminSvc.refund(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Received the refund request."));
    }

    @GetMapping(value = "/item/{itemId}")
    public Object getCodes(@PathVariable Long itemId) {
        return adminSvc.getCodeByItem(itemId);
    }

    @PostMapping(value = "/item/create")
    public ResponseEntity createItem(@RequestBody List<Item> request) {
        adminSvc.createItem(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Created " + request.size() + " items"));
    }

    @PostMapping(value = "/giftcard/create")
    public ResponseEntity addGiftCard(@RequestBody AddGiftCardRequest request) {
        adminSvc.addGiftCard(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("added " + request.getCodes().size() + " gift card"));
    }

    @GetMapping(value = "/items")
    public List<ItemDto> getCodes() {
        return adminSvc.getItemDetails();
    }
}
