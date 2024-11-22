package org.idirtrack.backend.exception;
import java.util.List;

import org.idirtrack.backend.handler.MyErrorRES;
import org.idirtrack.backend.handler.MyFieldError;

public class MyNotFoundException extends RuntimeException {
    private MyErrorRES response;

    public MyNotFoundException(String message) {
        super(message);
        this.response = MyErrorRES.builder().message(message).build();
    }

    public MyNotFoundException(String  message, String field) {
        List<MyFieldError> errors = List.of(MyFieldError.builder().field(field).message(message).build());
        this.response = MyErrorRES.builder().errors(errors).build();
    }

    public MyErrorRES getResponse() {
        return response;
    }

    public void setResponse(MyErrorRES response) {
        this.response = response;
    }
    
}
