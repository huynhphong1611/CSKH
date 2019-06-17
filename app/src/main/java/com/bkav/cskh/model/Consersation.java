package com.bkav.cskh.model;

import java.util.ArrayList;

public class Consersation {
    private ArrayList<Message> mListMessageData;
    public Consersation(){
        mListMessageData = new ArrayList<>();
    }

    public void setmListMessageData(Message message) {

        this.mListMessageData.add(message);
    }

    public ArrayList<Message> getListMessageData() {
        return mListMessageData;
    }
}