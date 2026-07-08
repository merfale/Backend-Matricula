package com.depazsotelo.matricula.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;


@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();


    public GoogleAuthenticatorKey generarSecreto() {
        return gAuth.createCredentials();
    }


    public String getOtpAuthUrl(String secret, String usuario) {
        return String.format(
                "otpauth://totp/MatriculaApp:%s?secret=%s&issuer=MatriculaApp",
                usuario, secret);
    }


    public boolean validarCodigo(String secret, int codigo) {
        return gAuth.authorize(secret, codigo);
    }
}