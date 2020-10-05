package com.example.myapplication3;
import android.view.ViewDebug;

public class CardItem {
    private String title;
    private String contents;
    private String account;
    private String contents2;
    public CardItem(String title, String account, String contents, String contents2){
        this.title = title;
        this.account = account;
        this.contents = contents;
        this.contents2 = contents2;
    }

    public String getTitle(){
        return title;
    }
    public String getAccount(){ return  account; }
    public String getContents(){
        return contents;
    }
    public String getContents2(){
        return contents2;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public void setContents2(String contents2){
        this.contents2 = contents2;
    }

}
