package com.canu.services;

import com.canu.model.FAQModel;
import com.canu.repositories.FAQRepository;
import com.canu.specifications.FAQFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FAQService {
    final FAQRepository faqRepo;

    public List<FAQModel> getListFAQs(FAQFilter filter){
        return faqRepo.findAll(filter);
    }

    public FAQModel initialFAQ(FAQModel model){
        return faqRepo.save(model);
    }
}
