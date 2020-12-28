package com.canu.repositories;

import com.canu.model.CanIModel;
import com.canu.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileModel, Long> {

    @Modifying
    @Query("delete from FileModel u where u.id in ?1 and u.owner.id = ?2")
    void deleteFilesWithIdsAndUser(List<Long> ids, Long userId);
}
