package io.bigbang.chatter.chatter;

import android.graphics.Color;

import java.util.Date;

/**
 * Created by Chris on 6/1/2015.
 */
public class Message {
    private String body;
    private Date date;
    private String sender;
    private int color;

    public Message(String messageBody, String sender, int color){
        this.body = messageBody;
        this.date = new Date();
        this.sender = sender;
        this.color = color;
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

    public void setColor(int color){
        this.color = color;
    }
    public int getColor() {
        return color;
    }

}
