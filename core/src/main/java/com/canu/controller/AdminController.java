package com.canu.controller;

import com.canu.dto.responses.Member;
import com.canu.model.CanIModel;
import com.canu.services.AdminService;
import com.canu.services.CanIService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    final private AdminService adminSvc;

    @GetMapping(value = "/member")
    public Object getDetail(Pageable p) {
        return adminSvc.getMembers(p);
    }
}
