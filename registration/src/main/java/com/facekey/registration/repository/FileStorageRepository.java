package com.facekey.registration.repository;

import com.facekey.registration.model.entity.FaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStorageRepository extends CrudRepository<FaceEntity, String> {
}
