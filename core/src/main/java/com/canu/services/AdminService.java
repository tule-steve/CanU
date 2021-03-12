package com.canu.services;

import com.canu.dto.responses.Member;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.StringJoiner;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final private CanURepository canURepo;
    final private CanIRepository caniRepo;
    final private CanIService caniSvc;

    final private EntityManager em;

    public Page<Member> getMembers(Pageable p){
//        p.getSort()
//        p.getPageNumber()
        StringBuilder sb = new StringBuilder();
        sb.append("select u.id as userId, u.first_name as firstName, u.last_name as lastName, u.email as email, u.created_at as createdAt, ");
        sb.append("   0 as createdJob, 0 as finishedJob, 0 as processingJob, u.cani_id as caniId ");
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
        Page<Member> memberList = canURepo.getMembership(p);
        memberList.forEach(r -> {
            if(r.getCaniId() != null){
                r.setCani(caniRepo.findById(r.getCaniId()).orElse(null));
                caniSvc.updateCanIResponse(r.getCani(), canURepo.findById(r.getUserId()).get());
            }

        });
        return memberList;
    }


}
