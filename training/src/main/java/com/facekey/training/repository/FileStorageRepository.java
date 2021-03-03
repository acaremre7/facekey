package com.facekey.training.repository;

import com.facekey.training.model.FaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStorageRepository extends CrudRepository<FaceEntity, Long> {
    List<FaceEntity> findByIsProcessed(boolean isProcessed);
}

