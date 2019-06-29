package com.yuan.blog.vo.Poem;

public class PoemResponse {
    private String status;
    private Data data;
    private String token;
    private String ipAddress;
    private String warning;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getIpAddress() {
        return ipAddress;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
    public String getWarning() {
        return warning;
    }
}
