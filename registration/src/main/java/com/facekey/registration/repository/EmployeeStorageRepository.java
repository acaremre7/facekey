package com.facekey.registration.repository;

import com.facekey.registration.model.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeStorageRepository extends CrudRepository<UserEntity, String> {
}
