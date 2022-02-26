package com.example.travellerproject.controllers;
import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpSession;

@Component
public class SessionValidator {

    private static final String LOGGED = "logged";

    public void isUserLogged(HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new UnauthorizedException("You must be logged first");
        }
    }

    public long isUserLoggedIn(HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new UnauthorizedException("You must be logged first");
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
            throw new BadRequestException("You are already logged in.");
        }
    }
}
