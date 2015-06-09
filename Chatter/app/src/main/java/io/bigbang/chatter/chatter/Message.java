package io.bigbang.chatter.chatter;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Chris on 6/1/2015.
 */
public class Message implements Parcelable{
    private String body;
    private String date;
    private String sender;
    private int color;

    public Message(String body, String sender, int color, String date){
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.color = color;
    }

    public Message(Parcel in){
        this.body = in.readString();
        this.sender = in.readString();
        this.date = in.readString();
        this.color = in.readInt();
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

    public String getDate(){
        return this.date;
    }

    public void setColor(int color){
        this.color = color;
    }
    public int getColor() {
        return color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(sender);
        dest.writeString(date);
        dest.writeInt(color);

    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
