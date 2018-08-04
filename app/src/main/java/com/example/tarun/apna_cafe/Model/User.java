package com.example.tarun.apna_cafe.Model;


public class User {

    private String name;
    private String password;
    private  String Phone;
    private  String IsStaff;
    private String secureCode;

    public User() {
    }

    public User(String name, String password , String secureCode) {
        this.name = name;
        this.password = password;
        IsStaff = "false";
        this.secureCode = secureCode;
    }

    public String getSecureCode () {
        return secureCode;
    }

    public void setSecureCode ( String secureCode ) {
        this.secureCode = secureCode;
    }

    public String getIsStaff () {
        return IsStaff;
    }

    public void setIsStaff ( String isStaff ) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
