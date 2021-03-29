package ru.vsu.cs.Crocodile.jwt;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTCustomSigner {

    @Value("${DEFAULT_SECRET}")
    private String secret;

    public JWTCustomSigner() {
    }


    public JWSSigner getSigner() {
        JWSSigner signer;
        try {
            signer = new MACSigner(secret);
        } catch (KeyLengthException e) {
            signer = null;
        }
        return signer;
    }
}