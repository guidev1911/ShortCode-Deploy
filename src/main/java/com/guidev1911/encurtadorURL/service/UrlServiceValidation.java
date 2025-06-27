package com.guidev1911.encurtadorURL.service;

import com.guidev1911.encurtadorURL.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.regex.Pattern;

@Service
public class UrlServiceValidation {

    @Autowired
    public UrlRepository repository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateUniqueShortCode() {
        String code;
        int attempts = 0;
        do {
            code = generateRandomCode();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Falha ao gerar um código curto único após várias tentativas.");
            }
        } while (repository.existsByShortCode(code));
        return code;
    }
    public String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
    public boolean isValidUrl(String url) {
        try {
            URL parsedUrl = new URL(url);

            String protocol = parsedUrl.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                return false;
            }

            String regex = "^(http|https)://[a-zA-Z0-9.-]+\\.[a-z]{2,}.*$";
            return Pattern.matches(regex, url);

        } catch (MalformedURLException e) {
            return false;
        }
    }

}
