package at.ac.tuwien.dsg.dsexamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;

import java.net.Socket;

import java.net.UnknownHostException;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

 /* 
 * This simple class for showing the execution of a centralized server for mutual exclusion
 *  A primitive illustrative example for distributed systems course
 *  No strong check on errors and no  rigorous software engineering requirements  (e.g., controling input parameters, exception handling, etc.)
 *  Written by Linh Truong
 */ 
public class CentralizedMutualExclusion {
    private int serverPort = 0;
    private String serverHost = null;
    boolean isServer = false;
    String myID = null;

    public CentralizedMutualExclusion(String host, int port, boolean server, String myID) throws IOException,
                                                                                                 InterruptedException {
        super();
        this.serverHost = host;
        this.serverPort = port;
        this.isServer = server;
        this.myID = myID;
        if (isServer) {
            runServer();
        }
        if (!myID.equalsIgnoreCase("null"))
            runClient();
        
    }

    private static Lock lock = new ReentrantLock();

    public void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverPort);
        System.out.println("I am the centralized server for mutual exclusion");
        while (true) {
            final Socket clientSocket = serverSocket.accept();
            new Thread(  new Runnable() {
                        public void run()  {
                        try {
                            askForPermission(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                     
            }).start();
        }
        
    }

    public void askForPermission(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientID = in.readLine();
        System.out.println("======== I am checking the lock for " + clientID + " ===========");
        lock.lock();
        //return the permission
        String reply = "OK";
        out.println(reply);
        System.out.println("I am waiting for " + clientID + " to release  the lock");
        //read data
        String release = in.readLine();
        System.out.println("======== OK " + clientID + " just released the lock=============");
        out.close();
        in.close();
        clientSocket.close();
        lock.unlock();

    }

    public void runClient() throws UnknownHostException, IOException, InterruptedException {
        //do 5 times
        for (int i = 0; i < 5; i++) {
        Socket socket = new Socket(serverHost, serverPort);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("I am " + myID + " and I am trying to get the permission");
        out.println(myID);
        String reply = in.readLine();
        if (reply != null) {
            System.out.println("I acquire the permission");
            System.out.println("I am doing some work");
            Thread.sleep((new Random()).nextInt(5000));
            out.println(myID);
            System.out.println("I release the permission");
        }
        out.close();
        in.close();
        socket.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //assume that arguments are [server host] [server port] ["yes" or "no" if it is a server] [id or "null" if it is a client]
        CentralizedMutualExclusion centralizedMutualExclusion = new CentralizedMutualExclusion(args[0], Integer.valueOf(args[1]), args[2].equalsIgnoreCase("yes" ), args[3]);
    }
}
