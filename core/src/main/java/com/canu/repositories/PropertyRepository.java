package com.canu.repositories;

import com.canu.model.MetadataPropertyModel;
import com.canu.model.SkillSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<MetadataPropertyModel, String>{

    List<MetadataPropertyModel> findDistinctByKeyIn(List<String> keyList);
}
