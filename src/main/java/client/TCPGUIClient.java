package client;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import network.TCPNetworkLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

@Slf4j
public class TCPGUIClient {

    // Provide networking functionality

    static JFrame f;

    static JFrame f1;


    static JFrame fViewAlbum;

    static JFrame fViewSearchAlbum;



    //lists
    static JList b;

    static JList b1;


    private JLabel artistListLabel;

    private JLabel artistListLabel1;



    private TCPNetworkLayer network;
    Gson gson = new Gson();

    // GUI components
    private final HashMap<String, Container> guiContainers = new HashMap<>();
    // Main gui window
    private JFrame mainFrame;
    // Main Font setting
    private Font font = new Font("Arial", Font.PLAIN, 16);

    // Panel for initial view
    private JPanel initialView;
    // Display for initial options - labels, text fields and button
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton loginButton;

    private JButton registerButton;


    // Panel for logged-in view
    private JPanel homePageView;
    private JButton homePageButton;

    private JButton logOut;

    private JButton goBackToHomePage;

    private JButton goBackToArtistPage;

    private JButton buttonToReviewAlbum;

    private JButton goBackToAlbumPage;



    private JButton sendEmail;


    private JButton allArtist;

    private JButton allAlbum;


    private JButton searchForArtist;

    private JButton searchForAlbum;

    private JButton searchForAlbumReview;



    private JButton getContentOfReceivedEmails;

    private JButton getContentOfSentEmails;

    private JButton getReceivedEmailById;

    private JButton getSentEmailById;


    private JPanel registerView;
    private JButton registerViewButton;

    private JLabel usernameLabel1;

    private JTextField usernameTextField1;

    private JLabel passwordLabel1;


    private JTextField passwordTextField1;


    private JLabel confirmPasswordLabel1;


    private JTextField confirmPasswordTextField1;

    private JLabel emailLabel;

    private JTextField emailTextField;

    // send Email

    private JPanel sendAlbumReviewView;
    private JButton completeReviewButton;


    private JLabel receiverEmailLabel;

    private JTextField receiverEmailTextField;

    private JLabel ratingLabel;


    private JTextField ratingTextField;


    private JLabel commentLabel;


    private JTextField commentTextField;


    // search for emails based on subject

    private JPanel searchEmailSubjectView;

    private JButton searchEmailButton;

    private JLabel artistSearchLabel;

    private JTextField artistSearchTextField;


    private JLabel albumSearchLabel;

    private JTextField albumSearchTextField;


    private JLabel albumReviewSearchLabel;

    private static JTextField albumReviewSearchTextField;

    // get content of retrieved emails based on email Id


    private JPanel contentReceivedEmailsView;

    private JButton getContentReceivedEmailsButton;

    private JLabel idLabel;

    private JTextField idTextField;


    // get content of sent emails

    private JPanel contentSentEmailsView;

    private JButton getContentSentEmailsButton;

    private JLabel id2Label;

    private JTextField id2TextField;

    // get retrieved email by id

    private JPanel getReceivedEmailByIdView;

    private JButton getReceivedEmailByIdButton;

    private JLabel emailIdLabel;

    private JTextField emailIdTextField;

    // get sent email by id

    private JPanel getSentEmailByIdView;

    private JButton getSentEmailByIdButton;

    private JLabel emailSentIdLabel;

    private JTextField emailSentIdTextField;




    // Use constructor to establish the components (parts) of the GUI
    public TCPGUIClient() {

        // Set up the main window
        configureMainWindow();

        // Set up the initial panel (the initial view on the system)
        // This takes in the username and password of the user
        configureInitialPanel();

        // Set up second panel
        configureHomePageView();

        // register view

        configureRegisterView();


        configureAddReviewPanel();


    }

    private static GridBagConstraints getGridBagConstraints(int col, int row, int width) {
        // Create a constraints object to manage component placement within a frame/panel
        GridBagConstraints gbc = new GridBagConstraints();
        // Set it to fill horizontally (component will expand to fill width)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Add padding around the component (Pad by 5 on all sides)
        gbc.insets = new Insets(5, 5, 5, 5);

        // Set the row position to the supplied value
        gbc.gridx = col;
        // Set the column position to the supplied value
        gbc.gridy = row;
        // Set the component's width to the supplied value (in columns)
        gbc.gridwidth = width;
        return gbc;
    }

    private void configureMainWindow() {
        // Create the main frame - this is the main window
        mainFrame = new JFrame("Basic Sample GUI");
        mainFrame.setSize(500, 400);
        // Set what should happen when the X button is clicked on the window
        // This approach will dispose of the main window but not shut down the program
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set the layout manager used for the main window
        mainFrame.setLayout(new CardLayout());

        // Add a listener to the overall window that reacts when window close action is requested
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Create the overall request object
                JsonObject requestJson = new JsonObject();
                // Add the request type/action and payload
                requestJson.addProperty("action", AuthUtils.EXIT);

                String request = gson.toJson(requestJson);
                network.send(request);

                // Wait to receive a response to the authentication request
                String response = network.receive();

                JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
                String result1 = jsonResponse1.get("message").getAsString();

                JOptionPane.showMessageDialog(initialView, result1, "Exiting System",
                        JOptionPane.INFORMATION_MESSAGE);

                try {
                    network.disconnect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Shutting down...");
                // Shut down the application fully
                System.exit(0);
            }
        });

        // Register the main window as a container in the system
        guiContainers.put("mainFrame", mainFrame);
    }

    // Set up initial panel (initial view)
    private void configureInitialPanel() {
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        initialView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("initialView", initialView);

        // Create text fields and associated labels to take in username and password
        // Username info
        usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);

        // Password info
        passwordLabel = new JLabel("Password: ");
        passwordField = new JTextField(15);

        // Create a button to log in user
        loginButton = new JButton("Log in");
        // Specify what the button should DO when clicked:

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });


        // Create a button to register user
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });



        // Add credential components to initial view panel in specific positions within the gridbag
        // Add username label and text field on first row (y = 0)
        initialView.add(usernameLabel, getGridBagConstraints(0, 0, 1));
        initialView.add(usernameField, getGridBagConstraints(1, 0, 1));
        // Add password label and text field on second row (y = 1)
        initialView.add(passwordLabel, getGridBagConstraints(0, 1, 1));
        initialView.add(passwordField, getGridBagConstraints(1, 1, 1));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        initialView.add(loginButton, getGridBagConstraints(0, 2, 2));

        initialView.add(registerButton, getGridBagConstraints(0, 3, 2));


        // Add empty space on fourth row (y = 3) spanning two columns (width = 2)
        initialView.add(new JPanel(), getGridBagConstraints(0, 4, 2));

    }

    private void configureHomePageView(){


        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        homePageView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("homePageView", homePageView);


        // View All Artist in the system

        allArtist = new JButton("View All Artist");
        // Specify what the button should DO when clicked:
        allArtist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allArtistView();
            }
        });


        // View All Albums in the system

        allAlbum = new JButton("View All Albums");
        // Specify what the button should DO when clicked:
        allAlbum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allAlbumView();
            }
        });


        // logout

        logOut = new JButton("Log Out");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutUser();
            }
        });


        // Add button on third row (y = 2) spanning two columns (width = 2)


        homePageView.add(allArtist, getGridBagConstraints(0, 2, 2));

        homePageView.add(allAlbum, getGridBagConstraints(0, 3, 2));

        homePageView.add(logOut, getGridBagConstraints(0, 4, 2));
    }

    private void showInitialView(){
        // Add config panel to the main window and make it visible
        mainFrame.add(initialView);
        mainFrame.setVisible(true);
    }

    private void showHomePageView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(homePageView);
        mainFrame.setVisible(true);
    }




    // register View

    private void configureRegisterView(){
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        registerView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("registerView", registerView);

        // Create text fields and associated labels to take in username and password
        // Username info
        usernameLabel1 = new JLabel("username: ");
        usernameTextField1 = new JTextField(15);

        passwordLabel1 = new JLabel("password: ");
        passwordTextField1 = new JTextField(15);


        confirmPasswordLabel1 = new JLabel("Confirm Password: ");
        confirmPasswordTextField1 = new JTextField(15);


        // Create a button to log in user
        registerViewButton = new JButton("Register");
        // Specify what the button should DO when clicked:
        registerViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRegisterButton();
            }
        });


        logOut = new JButton("Go Back To Login");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });

        // Add credential components to count view panel in specific positions within the gridbag
        // Add username label and text field on first row (y = 0)
        registerView.add(usernameLabel1, getGridBagConstraints(0, 0, 1));
        registerView.add(usernameTextField1, getGridBagConstraints(1, 0, 1));
        // Add password label and text field on second row (y = 1)
        registerView.add(passwordLabel1, getGridBagConstraints(0, 1, 1));
        registerView.add(passwordTextField1, getGridBagConstraints(1, 1, 1));

        //confirm password

        registerView.add(confirmPasswordLabel1, getGridBagConstraints(0, 2, 1));
        registerView.add(confirmPasswordTextField1, getGridBagConstraints(1, 2, 1));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        registerView.add(registerViewButton, getGridBagConstraints(0, 3, 2));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        registerView.add(logOut, getGridBagConstraints(0, 4, 2));
    }


    private void showRegisterView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(registerView);
        mainFrame.setVisible(true);
    }







    private void showSearchEmailSubjectView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(searchEmailSubjectView);
        mainFrame.setVisible(true);
    }




    public void start() throws IOException {
        network = new TCPNetworkLayer(AuthUtils.SERVER_HOST, AuthUtils.SERVER_PORT);
        network.connect();
        // Add the initial panel to the main window and display the interface
        showInitialView();
    }

    /*
     * All methods below this point provide application logic
     */
    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGIN);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        String response = network.receive();

        System.out.println(response);

        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        String result = jsonResponse.get("message").getAsString();

        // If the response matches the expected success message, treat user as authenticated
        if (response.equals(AuthUtils.LOGIN_SUCCESSFUL)) {
            JOptionPane.showMessageDialog(initialView, result, "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.remove(initialView);
            showHomePageView();

            log.info("User {} logged in", username);

            usernameField.setText("");
            passwordField.setText("");

            return;
        }

        JOptionPane.showMessageDialog(initialView, result, "Login Failed",
                JOptionPane.ERROR_MESSAGE);

        log.info("User {} failed logged in", username);



    }


    private void registerUser(){

        mainFrame.remove(initialView);
        showRegisterView();
    }


    private void configureAddReviewPanel(){
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        sendAlbumReviewView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("sendEmailView", sendAlbumReviewView);

        // Create text fields and associated labels to take in username and password
        // Username info


        ratingLabel = new JLabel("Rating: ");
        ratingTextField = new JTextField(15);


        commentLabel = new JLabel("Comment: ");
        commentTextField = new JTextField(15);


        // Create a button to log in user
        completeReviewButton = new JButton("Complete Review");
        // Specify what the button should DO when clicked:
        completeReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               addReviewForAlbum();
            }
        });


        goBackToHomePage = new JButton("Go Back To Home Page");
        // Specify what the button should DO when clicked:
        goBackToHomePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToHomePageSendEmail();
            }
        });

        goBackToArtistPage = new JButton("Go Back To Album Page");
        // Specify what the button should DO when clicked:
        goBackToArtistPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToAlbumPageAfterReview();
            }
        });



        logOut = new JButton("LogOut");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutSendEmail();
            }
        });


        sendAlbumReviewView.add(ratingLabel, getGridBagConstraints(0, 1, 1));
        sendAlbumReviewView.add(ratingTextField, getGridBagConstraints(1, 1, 1));

        sendAlbumReviewView.add(commentLabel, getGridBagConstraints(0, 2, 1));
        sendAlbumReviewView.add(commentTextField, getGridBagConstraints(1, 2, 1));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        sendAlbumReviewView.add(completeReviewButton, getGridBagConstraints(0, 3, 2));

        sendAlbumReviewView.add(goBackToArtistPage, getGridBagConstraints(0, 4, 2));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        sendAlbumReviewView.add(goBackToHomePage, getGridBagConstraints(0, 5, 2));


        sendAlbumReviewView.add(logOut, getGridBagConstraints(0, 6, 2));
    }


    private void showAddReviewView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        fViewAlbum.dispose();
        mainFrame.remove(homePageView);
        mainFrame.add(sendAlbumReviewView);
        mainFrame.setVisible(true);
    }

    private void logOutUser(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(homePageView);
        showInitialView();
    }


    private void goBackToLogin(){

        mainFrame.remove(registerView);
        showInitialView();
    }



    private void logOutSendEmail(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(sendAlbumReviewView);
        showInitialView();
    }


    private void logOutSearchEmail(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(searchEmailSubjectView);
        showInitialView();
    }

    private void logOutGetContent(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(contentReceivedEmailsView);
        showInitialView();
    }

    private void logOutGetContentSent(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(contentSentEmailsView);
        showInitialView();
    }

    private void logOutGetRetrievedEmailById(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(getReceivedEmailByIdView);
        showInitialView();
    }

    private void logOutGetSentEmailById(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGOUT);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
        String result1 = jsonResponse1.get("message").getAsString();

        JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                JOptionPane.INFORMATION_MESSAGE);

        mainFrame.remove(getSentEmailByIdView);
        showInitialView();
    }




    private void goBackToHomePageSendEmail(){

        mainFrame.remove(sendAlbumReviewView);
        showHomePageView();
    }




    private void goBackToHomePageSendEmail2(){

        mainFrame.remove(searchEmailSubjectView);
        showHomePageView();
    }


    private void goBackToHomePageGetContent(){

        mainFrame.remove(contentReceivedEmailsView);
        showHomePageView();
    }


    private void goBackToHomePageGetContentSent(){

        mainFrame.remove(contentSentEmailsView);
        showHomePageView();
    }

    private void goBackToHomePageGetEmailByIdRetrieved(){

        mainFrame.remove(getReceivedEmailByIdView);
        showHomePageView();
    }

    private void goBackToHomePageGetEmailByIdSent(){

        mainFrame.remove(getSentEmailByIdView);
        showHomePageView();
    }

    private void goToSearchEmailsPage(){

        mainFrame.remove(homePageView);
        showSearchEmailSubjectView();
    }

    private void goBackToHomePageEmailList(){

        f.dispose();
        showHomePageView();
    }


    private void goBackToHomePageEmailList2(){

        fViewAlbum.dispose();
        showHomePageView();
    }



    private void goBackToArtistPageAfterSearch(){

        f1.dispose();
        f.show();
    }

    private void goBackToAlbumPageAfterSearch(){

        fViewSearchAlbum.dispose();
        fViewAlbum.show();
    }


    private void goBackToAlbumPageAfterReview(){
        mainFrame.remove(sendAlbumReviewView);
        fViewAlbum.show();
    }

    private void showAlbumReview(){

        mainFrame.remove(initialView);
        mainFrame.add(sendAlbumReviewView);
        mainFrame.setVisible(true);
    }

    private void setRegisterButton(){
        String username = usernameTextField1.getText();
        String password = passwordTextField1.getText();
        String confirmPassword = confirmPasswordTextField1.getText();


        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        payload.addProperty("confirmPassword", confirmPassword);



        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.REGISTER);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        // formatting it nice for the user
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        String result = jsonResponse.get("message").getAsString();


        if (response.equalsIgnoreCase(AuthUtils.REGISTER_SUCCESSFUL)) {

            JOptionPane.showMessageDialog(initialView, result, "Register Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.remove(registerView);
            showHomePageView();

            log.info("User {} registered successful", username);

            usernameTextField1.setText("");
            passwordTextField1.setText("");
            confirmPasswordTextField1.setText("");

            System.out.println(response);
            return;

        }
        JOptionPane.showMessageDialog(initialView, result, "Register Failed",
                JOptionPane.ERROR_MESSAGE);
        log.info("User {} failed registration", username);

    }


    public static String [] grow(String [] data, int numExtraSlots){
        String [] larger = new String[data.length + numExtraSlots];
        for(int i = 0; i < data.length; i++){
            larger[i] = data[i];
        }
        return larger;
    }

    private void allArtistView(){

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.GET_ALL_ARTIST);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();


        if (response.equalsIgnoreCase(AuthUtils.INVALID) || response.equalsIgnoreCase(AuthUtils.YOU_HAVE_NO_ARTISTS) || response.equalsIgnoreCase(AuthUtils.NOT_LOGGED_IN)){

            JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
            String result1 = jsonResponse1.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "Retrieve Artist failed",
                    JOptionPane.INFORMATION_MESSAGE);

        }else {
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String result = jsonResponse.get("artists").getAsString();

            String[] artists = result.split("##");

            //create a new frame
            f = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            artistSearchLabel = new JLabel("Artist Name: ");
            artistSearchTextField = new JTextField(15);

            searchForArtist = new JButton("Search for Artist");
            // Specify what the button should DO when clicked:
            searchForArtist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchForArtist();
                }
            });

            p.add(artistSearchLabel, getGridBagConstraints(0, 1, 2));
            p.add(artistSearchTextField, getGridBagConstraints(1, 1, 2));
            p.add(searchForArtist, getGridBagConstraints(0, 2, 2));




            artistListLabel = new JLabel("List of all artist in the system");
            p.add(artistListLabel, getGridBagConstraints(0, 3, 2));

            String [] artistArray = grow(artists, artists.length);
            b = new JList(artistArray);
            b.setSelectedIndex(0);
            p.add(b);
            f.add(p);
            f.setSize(500,400);

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageEmailList();
                }
            });

            p.add(goBackToHomePage, getGridBagConstraints(0, 4, 2));


            logOut = new JButton("Log Out");
            // Specify what the button should DO when clicked:
            logOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logOutUser();
                }
            });



            p.add(logOut, getGridBagConstraints(0, 5, 2));
            f.show();

        }

    }


    private void allAlbumView(){

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.GET_ALL_ALBUM);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();


        if (response.equalsIgnoreCase(AuthUtils.INVALID) || response.equalsIgnoreCase(AuthUtils.YOU_HAVE_NO_ALBUMS) || response.equalsIgnoreCase(AuthUtils.NOT_LOGGED_IN)){

            JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
            String result1 = jsonResponse1.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "Retrieve Albums failed",
                    JOptionPane.INFORMATION_MESSAGE);

        }else {
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String result = jsonResponse.get("albums").getAsString();

            String[] artists = result.split("##");

            //create a new frame
            fViewAlbum = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            albumSearchLabel = new JLabel("Album Name: ");
            albumSearchTextField = new JTextField(15);

            searchForAlbum = new JButton("Search for Album");
            // Specify what the button should DO when clicked:
            searchForAlbum.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchForAlbum();
                }
            });

            p.add(albumSearchLabel, getGridBagConstraints(0, 1, 2));
            p.add(albumSearchTextField, getGridBagConstraints(1, 1, 2));
            p.add(searchForAlbum, getGridBagConstraints(0, 2, 2));



            artistListLabel = new JLabel("List of all Albums in the system");
            p.add(artistListLabel, getGridBagConstraints(0, 3, 2));

            String [] artistArray = grow(artists, artists.length);
            b = new JList(artistArray);
            b.setSelectedIndex(0);
            p.add(b);
            fViewAlbum.add(p);
            fViewAlbum.setSize(500,400);
            // go review album here

            albumReviewSearchLabel = new JLabel("Album Id: ");
            albumReviewSearchTextField = new JTextField(15);

            ratingLabel = new JLabel("Rating: ");
            ratingTextField = new JTextField(15);


            commentLabel = new JLabel("Comment: ");
            commentTextField = new JTextField(15);


            // Create a button to log in user
            completeReviewButton = new JButton("Complete Review");
            // Specify what the button should DO when clicked:
            completeReviewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addReviewForAlbum();
                }
            });



            p.add(albumReviewSearchLabel, getGridBagConstraints(0, 4, 1));
            p.add(albumReviewSearchTextField, getGridBagConstraints(1, 4, 1));

            p.add(ratingLabel, getGridBagConstraints(0, 5, 1));
            p.add(ratingTextField, getGridBagConstraints(1, 5, 1));

            p.add(commentLabel, getGridBagConstraints(0, 6, 1));
            p.add(commentTextField, getGridBagConstraints(1, 6, 1));


            // Add button on third row (y = 2) spanning two columns (width = 2)
            p.add(completeReviewButton, getGridBagConstraints(0, 7, 2));

            albumReviewSearchTextField.getText();
            ratingTextField.getText();
            commentTextField.getText();


            /*
            albumReviewSearchLabel = new JLabel("Enter Album to Review: ");
            albumReviewSearchTextField = new JTextField(15);

            searchForAlbumReview = new JButton("Go Review Album");
            // Specify what the button should DO when clicked:
            searchForAlbumReview.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchForAlbumToReview();
                }
            });

            p.add(albumReviewSearchLabel, getGridBagConstraints(0, 4, 2));
            p.add(albumReviewSearchTextField, getGridBagConstraints(1, 4, 2));
            p.add(searchForAlbumReview, getGridBagConstraints(0, 5, 2));

             */




            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageEmailList2();
                }
            });

            p.add(goBackToHomePage, getGridBagConstraints(0, 8, 2));


            logOut = new JButton("Log Out");
            // Specify what the button should DO when clicked:
            logOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logOutUser();
                }
            });



            p.add(logOut, getGridBagConstraints(0, 9, 2));
            fViewAlbum.show();

        }

    }




    private void searchForArtist(){

        String artist = artistSearchTextField.getText();

        JsonObject payload = new JsonObject();
        payload.addProperty("artist", artist);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.SEARCH_FOR_ARTIST);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();
        if (response.equals(AuthUtils.NO_ARTISTS_WITH_THIS_NAME) || response.equals(AuthUtils.INVALID) || response.equals(AuthUtils.EMPTY_ARTIST_NAME) || response.equals(AuthUtils.NOT_LOGGED_IN) || response.equals(AuthUtils.EMPTY_ARTIST_NAME)) {

            JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
            String result1 = jsonResponse1.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "No Artists with this name",
                    JOptionPane.ERROR_MESSAGE);

            log.info("No artist with name {}", artist);
        }else {
            // formatting it nice for the user
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String result = jsonResponse.get("artists").getAsString();

            String[] artists = result.split("##");

            //create a new frame
            f1 = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            artistListLabel = new JLabel("Searched artist with the name: "+artist);
            p.add(artistListLabel, getGridBagConstraints(0, 0, 1));

            String [] artistArray = grow(artists, artists.length);
            b = new JList(artistArray);
            b.setSelectedIndex(0);
            p.add(b);
            f1.add(p);
            f1.setSize(500,400);

            goBackToArtistPage = new JButton("Go Back To Artist Page");
            // Specify what the button should DO when clicked:
            goBackToArtistPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToArtistPageAfterSearch();
                }
            });
            p.add(goBackToArtistPage, getGridBagConstraints(0, 2, 2));

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageEmailList();
                }
            });
            p.add(goBackToHomePage, getGridBagConstraints(0, 3, 2));

            logOut = new JButton("Log Out");
            // Specify what the button should DO when clicked:
            logOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logOutUser();
                }
            });



            p.add(logOut, getGridBagConstraints(0, 4, 2));


            f1.show();



            artistSearchTextField.setText("");

        }
    }

    private void searchForAlbum(){

        String album = albumSearchTextField.getText();

        JsonObject payload = new JsonObject();
        payload.addProperty("album", album);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.SEARCH_FOR_ALBUM);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();
        if (response.equals(AuthUtils.NO_ALBUMS_WITH_THIS_NAME) || response.equals(AuthUtils.INVALID) || response.equals(AuthUtils.EMPTY_ALBUM_NAME) || response.equals(AuthUtils.NOT_LOGGED_IN) || response.equals(AuthUtils.EMPTY_ALBUM_NAME)) {

            JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
            String result1 = jsonResponse1.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "No Artists with this name",
                    JOptionPane.ERROR_MESSAGE);

            log.info("No album with name {}", album);
        }else {
            // formatting it nice for the user
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String result = jsonResponse.get("albums").getAsString();

            String[] artists = result.split("##");

            //create a new frame
            fViewSearchAlbum = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            artistListLabel = new JLabel("Searched album with the name: "+album);
            p.add(artistListLabel, getGridBagConstraints(0, 0, 1));

            String [] artistArray = grow(artists, artists.length);
            b = new JList(artistArray);
            b.setSelectedIndex(0);
            p.add(b);
            fViewSearchAlbum.add(p);
            fViewSearchAlbum.setSize(500,400);

            goBackToArtistPage = new JButton("Go Back To Album Page");
            // Specify what the button should DO when clicked:
            goBackToArtistPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToAlbumPageAfterSearch();
                }
            });
            p.add(goBackToArtistPage, getGridBagConstraints(0, 2, 2));

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageEmailList();
                }
            });
            p.add(goBackToHomePage, getGridBagConstraints(0, 3, 2));

            logOut = new JButton("Log Out");
            // Specify what the button should DO when clicked:
            logOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logOutUser();
                }
            });



            p.add(logOut, getGridBagConstraints(0, 4, 2));

            fViewSearchAlbum.show();
            albumSearchTextField.setText("");

        }

    }



    // search for album to review

    /*
    private void searchForAlbumToReview(){

        String album = albumReviewSearchTextField.getText();

        JsonObject payload = new JsonObject();
        payload.addProperty("album", album);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.SEARCH_FOR_ALBUM_FOR_USER_REVIEW);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();
        if (response.equals(AuthUtils.NON_NUMERIC_ID) || response.equals(AuthUtils.NO_ALBUMS_WITH_THIS_NAME) || response.equals(AuthUtils.INVALID) || response.equals(AuthUtils.EMPTY_ALBUM_NAME) || response.equals(AuthUtils.NOT_LOGGED_IN)) {

            JsonObject jsonResponse1 = gson.fromJson(response, JsonObject.class);
            String result1 = jsonResponse1.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "No Album with this Id",
                    JOptionPane.ERROR_MESSAGE);

            log.info("No album with name {}", album);
        }else if (response.equals(AuthUtils.ALBUM_FOUND)){
            // formatting it nice for the user

            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String result = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result, "Retrieve Albums success",
                    JOptionPane.INFORMATION_MESSAGE);

            albumReviewSearchTextField.setText("");


            showAddReviewView();

        }

    }

     */


    // add review for album


    private void addReviewForAlbum(){

        String albumId = albumReviewSearchTextField.getText();
        String rating = ratingTextField.getText();
        String comment = commentTextField.getText();


        JsonObject payload = new JsonObject();
        payload.addProperty("album", albumId);
        payload.addProperty("rating", rating);
        payload.addProperty("comment", comment);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.ADD_REVIEW);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

        // Wait to receive a response to the authentication request
        String response = network.receive();

        // formatting it nice for the user
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        String result = jsonResponse.get("message").getAsString();

        if (response.equalsIgnoreCase(AuthUtils.REVIEW_OF_ALBUM_SUCCESSFULLY_SENT) ) {

            JOptionPane.showMessageDialog(initialView, result, "Sent email Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            //mainFrame.remove(sendAlbumReviewView);
            //showHomePageView();

            log.info("User reviewed album with id {}", albumId);


            albumReviewSearchTextField.setText("");
            ratingTextField.setText("");
            commentTextField.setText("");

            System.out.println(response);
            return;

        }else if(response.equals(AuthUtils.NON_NUMERIC_ID) || response.equals(AuthUtils.INVALID) || response.equals(AuthUtils.REVIEW_ALREADY_EXIST) || response.equals(AuthUtils.NOT_LOGGED_IN) || response.equals(AuthUtils.RATING_OVER)){

            JOptionPane.showMessageDialog(initialView, result, "Sent email failed",
                    JOptionPane.ERROR_MESSAGE);
            log.info("User already reviewed album {}", albumReviewSearchTextField.getText());
        }


    }


    private void setStandardFonts(){
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
    }

    private void updateContainers() {
        for (Container c : guiContainers.values()) {
            for (Component component : c.getComponents()) {
                // Set the font in the component
                component.setFont(font);
            }
            // Revalidate and repaint the container
            c.revalidate();
            c.repaint();
        }
    }
    // GUI runner
    public static void main(String[] args) {
        // Create an instance of the GUI
        TCPGUIClient TCPGUIClient = new TCPGUIClient();
        // Start the GUI - this will trigger the application to be made visible
        try{
            TCPGUIClient.start();
        }catch(UnknownHostException e){
            System.out.println("Hostname could not be found. Please contact system administrator");
        }catch(SocketException e){
            System.out.println("Socket exception occurred. Please try again later.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
