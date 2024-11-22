package com.idirtrack.vehicle_service.basic;

public class BasicException extends Exception {

    private BasicResponse response;
    public BasicException(String message) {
        super(message);
    }

    public BasicException(BasicResponse response) {
        this.response = response;
    }

    public BasicResponse getResponse() {
        return response;
    }

    public void setResponse(BasicResponse response) {
        this.response = response;
    }
    

    
}
