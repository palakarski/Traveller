package com.example.travellerproject.controllers;

import com.example.travellerproject.exeptions.AuthenticationExeption;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.ErrorDTO;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExeptionControler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = BadRequestExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequest(Exception e){
            ErrorDTO  errorDTO= new ErrorDTO();
            errorDTO.setMessage(e.getMessage());
            errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            e.printStackTrace();
            return errorDTO;
    }

    @ExceptionHandler(value = UnauthorizedExeption.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleUnautorized(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        e.printStackTrace();
        return errorDTO;
    }
    @ExceptionHandler(value = NotFoundExeption.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleNotFound(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        e.printStackTrace();
        return errorDTO;
    }
    @ExceptionHandler(value = UnsupportedMediaTypeStatusException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public ErrorDTO handleUnsupported(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        e.printStackTrace();
        return errorDTO;
    }
    @ExceptionHandler(value = AuthenticationExeption.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleAuthentication(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value());
        e.printStackTrace();
        return errorDTO;
    }
    @ExceptionHandler(value = InvalidDefinitionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleInternal(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        e.printStackTrace();
        return errorDTO;
    }
}
