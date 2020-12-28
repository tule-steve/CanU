package com.canu.repositories;

import com.canu.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long>{
    List<TagModel> findAllByTagIn(Set<String> tag);
}
