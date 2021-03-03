package com.facekey.training.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "face")
public class FaceEntity {

    private String fileName;
    private int userId;
    private boolean isProcessed;

    public FaceEntity() {
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

    @Column(name = "is_processed", nullable = false)
    public boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(boolean processed) {
        isProcessed = processed;
    }
}

