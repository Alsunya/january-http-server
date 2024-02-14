package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.http.server.processors.HelloWorldRequestProcessor;
import ru.otus.java.basic.http.server.processors.OperationAddRequestProcessor;
import ru.otus.java.basic.http.server.processors.RequestProcessor;
import ru.otus.java.basic.http.server.processors.UnknownRequestProcessor;
import ru.otus.java.basic.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private Map<String, RequestProcessor> router;
    private RequestProcessor unknownRequestProcessor;

    public Dispatcher() {
        this.router = new HashMap<>();
        this.router.put("GET /add", new OperationAddRequestProcessor());
        this.router.put("GET /hello_world", new HelloWorldRequestProcessor());
        this.router.put("POST /body", new PostBodyDemoRequestProcessor());
        this.router.put("GET /json", new JsonRequestProcessor());
        this.unknownRequestProcessor = new UnknownRequestProcessor();
    }

    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        LOGGER.info("Выполняется запрос по URI: {}", httpRequest.getUri());
        if (!router.containsKey(httpRequest.getRoute())) {
            unknownRequestProcessor.execute(httpRequest, output);
            LOGGER.error("Неизвестный URI");
            return;
        }
        router.get(httpRequest.getRoute()).execute(httpRequest, output);
        RequestProcessor requestProcessor = router.get(httpRequest.getRoute());
        LOGGER.info("Выполняется запрос: {}", requestProcessor.getClass().getSimpleName());
        requestProcessor.execute(httpRequest, output);
        LOGGER.info("Запрос успешно выполнен");
    }
}
