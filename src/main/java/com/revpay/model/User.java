package com.revpay.model;

import java.time.Instant;

public class User {

    private long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String accountType;
    private boolean locked;
    private Instant createdAt;
    private Instant updatedAt;

    //constructor
    public User(){

    }

    public User(String username,String fullName,String email,String phone,String passwordHash,String accountType){

        this.username =  username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.accountType =accountType;
        this.locked = false;

    }

    //getters and setters

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public String getAccountType(){
        return accountType;
    }

    public void setAccountType(String accountType){
        this.accountType = accountType;
    }

    public boolean isLocked(){
        return locked;
    }

    public void setLocked(boolean locked){
        this.locked = locked;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt){
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt){
        this.updatedAt = updatedAt;
    }

    public String toString(){
        return "User{" +
                "id="+id+
                "username="+username+
                "fullname="+fullName+
                "email="+email+
                "phone="+phone+
                "accountType="+accountType+
                "locked="+locked+"}";


    }
}
