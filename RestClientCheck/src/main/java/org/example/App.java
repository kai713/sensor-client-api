package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final String API_URL_REGISTER_USER = "http://localhost:8080/auth/register";
    private static final String API_URL_LOGIN_USER = "http://localhost:8080/auth/login";
    private static final String API_URL_MEASUREMENTS = "http://localhost:8080/measurements/add";
    private static final String API_URL_SENSOR = "http://localhost:8080/sensors/registration";
    private static String BEARER_TOKEN = "";
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        registerUser();

        loginAndFetchToken();

        registerSensor();

        for (int i = 0; i < 100; i++) {
            sendPostRequest();
        }
    }

    private static void registerUser() {
        try {
            URL url = new URL(API_URL_REGISTER_USER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = "{ \"name\": \"name\", \"phoneNumber\": \"phone\", \"email\": \"email@gmail.com\", \"password\": \"password\" }";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loginAndFetchToken() {
        try {
            URL url = new URL(API_URL_LOGIN_USER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = "{ \"email\": \"email@gmail.com\", \"password\": \"password\" }";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String jsonResponse = response.toString();
                    Pattern pattern = Pattern.compile("\"token\":\"(.*?)\"");
                    Matcher matcher = pattern.matcher(jsonResponse);

                    if (matcher.find()) {
                        String accessToken = matcher.group(1);
                        System.out.println("Access Token: " + accessToken);
                        BEARER_TOKEN = accessToken;
                    } else {
                        System.out.println("Токен не найден в ответе сервера.");
                    }
                }
            } else {
                System.out.println("Ошибка при получении токена.");
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void registerSensor() {
        try {
            URL url = new URL(API_URL_SENSOR);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
            conn.setDoOutput(true);

            String requestBody = String.format(
                    "{ \"name\": \"SensorName\" }"
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPostRequest() {
        try {
            URL url = new URL(API_URL_MEASUREMENTS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
            conn.setDoOutput(true);

            int value = RANDOM.nextInt(101);
            int raining = RANDOM.nextInt(2);

            String requestBody = String.format(
                    "{ \"value\": %d, \"isRaining\": %d, \"sensor\": { \"name\": \"SensorName\" } }",
                    value, raining
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
