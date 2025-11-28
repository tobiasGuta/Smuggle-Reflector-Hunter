package com.bughunter;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;

import java.util.Arrays;
import java.util.List;

public class SmuggleReflector implements BurpExtension {

    private MontoyaApi api;

    private static final List<String> LEAKY_HEADERS = Arrays.asList(
            "Transfer-Encoding",
            "X-Forwarded-Host",
            "Via",
            "X-Cache",
            "Server-Timing",
            "X-Rewrite-URL",
            "X-Original-URL",
            "X-Forwarded-Proto",
            "Age",
            "Upgrade",
            "X-Custom-IP-Authorization"
    );

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("Smuggle & Reflector Hunter");
        api.http().registerHttpHandler(new MyHttpHandler());
        api.logging().logToOutput("Extension Loaded! Watching for Leaky Headers and Reflections...");
    }

    private class MyHttpHandler implements HttpHandler {

        // FIX 1: Name changed from 'handleRequestToBeSent' to 'handleHttpRequestToBeSent'
        @Override
        public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
            return RequestToBeSentAction.continueWith(requestToBeSent);
        }

        // FIX 2: Name changed from 'handleResponseReceived' to 'handleHttpResponseReceived'
        @Override
        public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {

            Annotations annotations = responseReceived.annotations();
            String notes = "";
            boolean isReflected = false;
            boolean isLeaky = false;

            // --- FEATURE 1: Leaky Header Spotter (Orange) ---
            for (String header : LEAKY_HEADERS) {
                if (responseReceived.hasHeader(header)) {
                    isLeaky = true;
                    notes += "[Header: " + header + "] ";
                }
            }

            // --- FEATURE 2: Reflection Detector (Blue) ---
            HttpRequest initiatingRequest = responseReceived.initiatingRequest();

            if (initiatingRequest != null) {
                String responseBody = responseReceived.bodyToString();
                for (ParsedHttpParameter param : initiatingRequest.parameters()) {
                    if (param.type() == HttpParameterType.URL) {
                        String value = param.value();
                        if (value.length() >= 4) {
                            if (responseBody.contains(value)) {
                                isReflected = true;
                                notes += "[Reflected: " + param.name() + "] ";
                            }
                        }
                    }
                }
            }

            if (isReflected) {
                annotations = annotations.withHighlightColor(HighlightColor.BLUE);
                annotations = annotations.withNotes(notes);
            }
            else if (isLeaky) {
                annotations = annotations.withHighlightColor(HighlightColor.ORANGE);
                annotations = annotations.withNotes(notes);
            }

            return ResponseReceivedAction.continueWith(responseReceived, annotations);
        }
    }
}