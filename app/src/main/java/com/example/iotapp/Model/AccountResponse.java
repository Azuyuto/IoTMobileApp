package com.example.iotapp.Model;

import java.util.List;

public class AccountResponse {
    public String username;
    public String email;
    public String password;
    public String token;
    public List<DeviceResponse> devices;
}
