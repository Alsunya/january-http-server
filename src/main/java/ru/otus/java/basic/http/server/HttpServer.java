package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private ExecutorService executorService;
    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class);

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void start() {
        LOGGER.info("Запуск HTTP сервера на порту {}", port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен...");
            while (true) {
                try{
                    Socket socket = serverSocket.accept();
                    executorService.execute(() -> handleClient(socket));
                } catch (IOException e) {
                    LOGGER.error("Ошибка подключения клиента", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка запуска сервера", e);
        } finally {
            executorService.shutdown();
            LOGGER.info("Работа сервера завершена");
        }
    }

    private void handleClient(Socket socket) {
        try {
            LOGGER.info("Обработка клиентского соединения...");
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            String rawRequest = new String(buffer, 0, n);
            HttpRequest httpRequest = new HttpRequest(rawRequest);
            dispatcher.execute(httpRequest, socket.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("Ошибка обработки клиентского соединения", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Ошибка закрытия клиентского сокета", e);
            }
        }
    }
}
