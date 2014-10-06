package at.ac.tuwien.dsg.dsexamples;

import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetSocketAddress;

public class SimpleHelloHTTPServer implements HttpHandler {
    String serverName ="";
    public SimpleHelloHTTPServer(String serverName) {
        
        super();
        this.serverName = serverName;
    }

    public void handle(HttpExchange exchange) throws IOException {
        //the goal is simple so ignore the input
        //return a simple message
        String response = "My name is:  " + serverName;
        exchange.sendResponseHeaders(200, response.length());
        OutputStream out = exchange.getResponseBody();
        out.write(response.getBytes());
        out.close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("I am "+args[1]+", listening at "+args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.valueOf(args[0])),1);
        server.createContext("/", new SimpleHelloHTTPServer(args[1]));
        server.setExecutor(null); 
        server.start();
    }

}
