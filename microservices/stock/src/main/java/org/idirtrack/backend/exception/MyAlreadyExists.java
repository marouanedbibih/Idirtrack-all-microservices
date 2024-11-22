package org.idirtrack.backend.exception;

import java.util.List;

import org.idirtrack.backend.handler.MyErrorRES;
import org.idirtrack.backend.handler.MyFieldError;

public class MyAlreadyExists extends RuntimeException {
    private MyErrorRES response;

    public MyAlreadyExists(String message) {
        super(message);
        this.response = MyErrorRES.builder().message(message).build();
    }

    public MyAlreadyExists(String message, String field) {
        this.response = MyErrorRES.builder()
                .errors(List.of(MyFieldError.builder().field(field).message(message).build())).build();
    }

    public MyErrorRES getResponse() {
        return response;
    }

    public void setResponse(MyErrorRES response) {
        this.response = response;
    }

}
