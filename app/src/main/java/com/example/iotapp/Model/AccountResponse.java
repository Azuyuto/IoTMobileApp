package com.example.iotapp.Model;

import java.util.List;

public class AccountResponse extends BasicResponse {
    public Integer id;
    public String username;
    public String email;
    public String token;
    public List<DeviceResponse> userDevices;
}
