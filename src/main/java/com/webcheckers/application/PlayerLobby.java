package com.webcheckers.application;

import com.webcheckers.model.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PlayerLobby
{
    static final String USERNAME_TAKEN = "This username is already taken. Please choose another one.";
    static final String USERNAME_INVALID = "This username is invalid. It contains characters that are not allowed in a username.";
    public static List<String> usernames = new ArrayList<>();
    static Player currentPlayer;
    static UsernameResult result;

    public synchronized static boolean addUsername(String username){
        usernames.add(username);
        currentPlayer = new Player(username);
        return true;
    }

    public synchronized UsernameResult isValidUsername(String username)
    {
        //make sure that the username contains letters and numbers and spaces only
        if(!username.contains("[^a-zA-z0-9]+") || username.contains("\"<([{\\\\^-=$!|]})?*+.>\""))
        {
            result = UsernameResult.INVALID;
            return result;
        }
        //if the arrayList.size() == 0 then there are no users in the playerLobby
        //and the username can be any username
        else if(usernames.size() == 0)
        {
            addUsername(username);
            result = UsernameResult.AVAILABLE;
        }
        //if there are already people in playerlobby
        else
        {
            //username already exists
            if(usernames.contains(username))
            {
                result = UsernameResult.TAKEN;
            }
            //username does not exist in lobby and is a valid username
            else
                {
                //if there are people in the lobby but chose an acceptable username
                addUsername(username);
                result = UsernameResult.AVAILABLE;
            }
        }
        return result;
    }

    public synchronized String getUsername(){
        return currentPlayer.getUsername();
    }

    public synchronized UsernameResult getUsernameResult(){
        return result;
    }
    public enum UsernameResult {TAKEN, AVAILABLE, INVALID}
}