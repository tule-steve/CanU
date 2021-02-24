package com.canu.repositories;

import com.canu.model.CanIModel;
import com.canu.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileModel, Long> {
}
