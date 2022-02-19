package com.example.travellerproject.controllers;

import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionValidator {

    private static final String LOGGED = "logged";


    public long isUserLogedIn (HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new UnauthorizedExeption("You must be logged first");
        }
        return (Long) session.getAttribute(LOGGED);
    }

    public void userLogsIn(HttpSession session, long userId){
        session.setAttribute(LOGGED, userId);
    }

    public void userLogsOut(HttpSession session){
        session.setAttribute(LOGGED, null);
    }

    public void isAlreadyLogged(HttpSession session) {
        if(!session.isNew()&&session.getAttribute(LOGGED)!=null){
            throw new BadRequestExeption("You are already logged in.");
        }
    }
}
