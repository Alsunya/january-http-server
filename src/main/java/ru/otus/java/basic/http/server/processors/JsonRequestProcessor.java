package ru.otus.java.basic.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.http.server.CustomJsonObject;
import ru.otus.java.basic.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonRequestProcessor implements RequestProcessor{
    CustomJsonObject jsonObject = new CustomJsonObject("value1", "value2", "value3");
    String jsonResponse = new Gson().toJson(jsonObject);
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        String response = "HTTP/1.1 200 OK\n" +
                "Content-Type: application/json\r\n\r\n" + jsonResponse;
        output.write(response.getBytes(StandardCharsets.UTF_8));

    }
}
