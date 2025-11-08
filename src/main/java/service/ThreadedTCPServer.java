package service;

import persistence.ArtistDaoImpl;
import persistence.UserDaoImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadedTCPServer {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30L;
    private static final int QUEUE_CAPACITY = 50;


    public static void main(String[] args) {


        ExecutorService clientHandlerPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try (ServerSocket connectionSocket = new ServerSocket(UserUtilities.PORT)){
            UserDaoImpl userDao = new UserDaoImpl("database.properties");
            ArtistDaoImpl artistDao = new ArtistDaoImpl("database.properties");

            String username = new String();

            boolean validServerSession = true;
            while(validServerSession){
                Socket clientDataSocket = connectionSocket.accept();
                TCPServer clientHandler = new TCPServer(clientDataSocket, userDao, artistDao, username);
                clientHandlerPool.submit(clientHandler);
            }
        }catch (IOException e){
            System.out.println("Connection socket cannot be established");
        }finally {
            clientHandlerPool.shutdown();
        }
    }
}
