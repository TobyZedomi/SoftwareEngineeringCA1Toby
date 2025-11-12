package service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import commands.Command;
import commands.CommandFactory;
import lombok.extern.slf4j.Slf4j;
import model.*;
import network.TCPNetworkLayer;
import persistence.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class TCPServer implements Runnable {

    private Socket clientDataSocket;
    private TCPNetworkLayer networkLayer;
    private UserDaoImpl userDao;

    private ArtistDaoImpl artistDao;

    private GenreDaoImpl genreDao;

    private AlbumDaoImpl albumDao;

    private ReviewDaoImpl reviewDao;
    private AlbumReviewDaoImpl albumReviewDao;

    private ArtistReviewDaoImpl artistReviewDao;

    private static String username;

    private static AlbumReview albumReview;
    private static String email;
    private final Gson gson = new Gson();


    public TCPServer(Socket clientDataSocket, UserDaoImpl userDao, ArtistDaoImpl artistDao, GenreDaoImpl genreDao, AlbumDaoImpl albumDao, ReviewDaoImpl reviewDao, AlbumReviewDaoImpl albumReviewDao, ArtistReviewDaoImpl artistReviewDao, String username) throws IOException {
        this.clientDataSocket = clientDataSocket;
        this.networkLayer = new TCPNetworkLayer(clientDataSocket);

        this.userDao = userDao;
        this.artistDao = artistDao;
        this.genreDao = genreDao;
        this.albumDao = albumDao;
        this.reviewDao = reviewDao;
        this.albumReviewDao = albumReviewDao;
        this.artistReviewDao = artistReviewDao;
        this.username = username;
    }

    public void run() {

        try {
            boolean validClientSession = true;
            boolean loginStatus = false;


            while (validClientSession) {

                String request = networkLayer.receive();
                System.out.println("Request: " + request);

                JsonObject jsonResponse = null;

                JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);


                Command c = new CommandFactory().createCommand(networkLayer, jsonRequest,
                        jsonResponse, userDao, username,  loginStatus, email,  artistDao, albumDao, albumReviewDao, artistReviewDao, reviewDao, genreDao, validClientSession);

                c.execute();

                /*
                String response = gson.toJson(jsonResponse);
                // Send response
                networkLayer.send(response);

                 */

            }

            networkLayer.disconnect();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }

}
