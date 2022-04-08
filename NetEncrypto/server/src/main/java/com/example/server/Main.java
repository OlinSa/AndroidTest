package com.example.server;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.crypto.RSA;

import http.HttpCallback;
import http.HttpService;

public class Main {
    private static String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3bpezyaoJEcmGOHKopPeL+zJh\n" +
            "r4hMzDyFONJyhc2IpIGF65E13Otc4oeWVNfPcRmYxCWvuSMo8EMdBTt+VHBv4Hyz\n" +
            "EVE79nETR69Qu4Crlm5Aokuv8oelcdR9PvNWtRhsy59LFj0y+1Qr9gUuOzTKgW6b\n" +
            "wuL5BrF+vxkQx/l8XwIDAQAB";
    private static String PRIV_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALdul7PJqgkRyYY4\n" +
            "cqik94v7MmGviEzMPIU40nKFzYikgYXrkTXc61zih5ZU189xGZjEJa+5IyjwQx0F\n" +
            "O35UcG/gfLMRUTv2cRNHr1C7gKuWbkCiS6/yh6Vx1H0+81a1GGzLn0sWPTL7VCv2\n" +
            "BS47NMqBbpvC4vkGsX6/GRDH+XxfAgMBAAECgYBx21SQhSFk5cSH6mvJIDSDj9Uy\n" +
            "60iYGsGKE74U1dbA9RNsc867dzgkfHuapWkbWuF/gGjADRO06oVN8xx9ip4K1bwg\n" +
            "sJxSqk4BN5ko8jpnGTQskId6SxdDQN8FkMqGuEYNuCRQNv2mDRhFVipZLqYIpEOy\n" +
            "s8q6Lkzne8M12V8KAQJBAOkDhmEZnR2xb7hP4JaM5WMro6j+b/7/sLiaM7ZHQ6qG\n" +
            "OlFKKHqYufNvzs7rFck+mmlTce3uNTgtlEHiQwA1vQECQQDJhvD8lbh38VoIeewv\n" +
            "x9VOa3no/vHpkMOkahf+xO98co4eKZv+ve9H+s14dknNc1HKz6uzdHDhoifxrW6+\n" +
            "U1lfAkAIFck54UukVh3MRWlDkAv9juwU1w9Hx9N39FHLB1n/trybSXlyF46MNdr1\n" +
            "Lw8IdpWhBfY0DUnncx1r09ADV9oBAkBdnmdjH3aDmcZWe7VB+RZZiMooeA8PjuWk\n" +
            "AnlFLF2ItXrZ/kBulfOLv7ImDOV6IOFVUyYThrtNxlPCDOyOVBAHAkA64/3N0+G+\n" +
            "Ea7V8R0jhmJBJFXnsFfg1FH2f1DUd2kPEtifimtjg9ZV+0ngBdxpoy1fNMgn+WNu\n" +
            "/PL+8nrH8JlI";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        int content = 123456;
        String encrypted = RSA.encrypto(content, PUB_KEY);
        System.out.println("raw="+content);
        System.out.println("entryto="+encrypted);
        System.out.println("decrypt="+RSA.decrypt(encrypted, PRIV_KEY));
        HttpService service = new HttpService(new HttpCallback() {
            @Override
            public byte[] onResponse(String request) {
                return "HttpService".getBytes();
            }
        });
        service.startHttpServer();
    }
}


