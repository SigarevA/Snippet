package ru.vsu.cs.Crocodile.CustomException;

import javax.naming.AuthenticationException;

public class TokenExpiredAuthenticationException extends AuthenticationException {
    public TokenExpiredAuthenticationException() {
        super();
    }
}
