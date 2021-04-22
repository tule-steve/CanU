package com.canu.repositories;

import com.canu.model.MetadataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataModel, String>{

    List<MetadataModel> findDistinctByKeyIn(List<String> keyList);
}
