package persistence;

import model.User;

import java.util.ArrayList;

public interface IUserDao {

    ArrayList<User> getAllUsers();

    int registerUser(User newUser);

    boolean checkIfUserExist(String username);

    boolean checkIfPasswordsAreTheSame(String password, String confirmPassword);

    boolean checkIfPasswordsMatchRegex(String password, String confirmPassword);


    boolean loginUser(String username, String password);

    public User findUserByUsername(String username);
}
