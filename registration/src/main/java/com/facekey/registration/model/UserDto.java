package com.facekey.registration.model;

public class UserDto extends CredentialsDto {

    private String firstName;
    private String lastName;
    private String adId;
    private int userId;


    public UserDto(String userName, String password, String firstName, String lastName, String adId, int userId) {
        super(userName, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.adId = adId;
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getUserId() {
        return userId;
    }
}
