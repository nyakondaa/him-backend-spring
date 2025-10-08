package Him.admin.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AuthServiceException extends AuthExceptions {
    public AuthServiceException(String message) {
        super(message);
    }
}
