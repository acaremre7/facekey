package com.facekey.registration.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "face")
public class FaceEntity {

    private String fileName;
    private int userId;

    public FaceEntity() {
    }

    public FaceEntity(String fileName, int userId) {
        this.fileName = fileName;
        this.userId = userId;
    }

    @Id
    @Column(name = "file_name", unique = true, nullable = false)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "user_id", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
