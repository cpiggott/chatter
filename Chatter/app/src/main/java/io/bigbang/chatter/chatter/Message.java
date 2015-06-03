package io.bigbang.chatter.chatter;

import java.util.Date;

/**
 * Created by Chris on 6/1/2015.
 */
public class Message {
    private String body;
    private Date date;
    private String sender;

    public Message(String messageBody, String sender){
        this.body = messageBody;
        this.date = new Date();
        this.sender = sender;
    }

    public void setBody(String messageBody){
        this.body = messageBody;
    }

    public String getBody(){
        return this.body;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return this.sender;
    }

    public Date getDate(){
        return this.date;
    }
}
