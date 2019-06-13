package com.akoo.common.util.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.net.InetSocketAddress;


/**
 * <pre>
 * @version 1.0
 * Description : 利用Jetty实现简单的嵌入式Httpserver
 * </pre>
 */
public class HttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private Server server;
    private InetSocketAddress address;
    private ServletContextHandler servletHandler;

    public HttpServer(String serverName, InetSocketAddress address) {
        this.address = address;
        servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/" + serverName);
    }

    public void addServlet(String name, HttpServlet servlet) {
        servletHandler.addServlet(new ServletHolder(servlet), "/" + name);
    }

    public void start() {
        try {
            // 进行服务器配置
            server = new Server(address);
            server.setStopAtShutdown(true);
            server.setHandler(servletHandler);
            LOGGER.info("server start.");
            // 启动服务器
            server.start();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void shutdown() {
        try {
            if (server != null)
                server.stop();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
