package com.canu.repositories;

import com.canu.model.UserPropertyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPropertyRepository
        extends JpaRepository<UserPropertyModel, Long> , JpaSpecificationExecutor<UserPropertyModel> {

}
