package com.akhi.islamiclikee.Model;

public class Password {
    private String Password;

    public Password(String password) {
        Password = password;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "Passwords{" +
                "Password='" + Password + '\'' +
                '}';
    }
    public Password(){
    }

}
