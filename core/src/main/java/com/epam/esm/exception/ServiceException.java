package com.epam.esm.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException{
    private long id;
    private String concreteMessage;

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(String message, long id){
        super(message);
        this.id = id;
    }

    public ServiceException(String message, String concreteMessage){
        super(message);
        this.concreteMessage=concreteMessage;
    }

    public String getId(){
        if(id!=0){
            return String.valueOf(id);
        }
        return "";
    }

    public String getConcreteMessage(){
        if(!concreteMessage.isEmpty()){
            return concreteMessage;
        }
        return "";
    }
}
