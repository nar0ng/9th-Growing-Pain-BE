package cotato.growingpain.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appCustomException(AppException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode() + " " + e.getErrorCode().getMessage());
    }
}