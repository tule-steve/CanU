package com.canu.services;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.responses.Member;
import com.canu.dto.responses.Token;
import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.security.config.TokenProvider;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final private CanURepository canURepo;

    final private EntityManager em;

    public List<Member> getMembers(Pageable p){
//        p.getSort()
//        p.getPageNumber()
        StringBuilder sb = new StringBuilder();
        sb.append("select u.id as userId, u.name, u.email as email, u.created_at as createdAt, ");
        sb.append("   0 as createdJob, 0 as finishedJob, 0 as processingJob ");
        sb.append(" from user u ");
        if(p.getSort() != Sort.unsorted()){
            sb.append(" order by ");
            StringJoiner joiner = new StringJoiner(", ");
            for (Sort.Order order : p.getSort())
            {
                CharSequence orderCmd = order.getProperty() + " " + order.getDirection().toString();
                joiner.add(orderCmd);
            }
            sb.append(joiner.toString());
        }
        Query q = this.em.createNativeQuery(sb.toString(), "MemberMapping");
        this.em.getEntityManagerFactory().addNamedQuery("CanUModel.getMembership", q);
        return canURepo.getMembership(p);
    }


}
