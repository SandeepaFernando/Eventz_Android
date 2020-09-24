package com.example.eventz.eventInfo;

public class CommentInfo {
    private String name_comment;
    private String date_comment;
    private String comment_st;
    private String userNameSP;
    private String userNameComment;

    public CommentInfo(String name_comment, String date_comment, String comment_st, String userNameSP, String userNameComment) {
        this.name_comment = name_comment;
        this.date_comment = date_comment;
        this.comment_st = comment_st;
        this.userNameSP = userNameSP;
        this.userNameComment = userNameComment;
    }

    public String getName_comment() {
        return name_comment;
    }

    public String getDate_comment() {
        return date_comment;
    }

    public String getComment_st() {
        return comment_st;
    }

    public String getUserNameSP() {
        return userNameSP;
    }

    public String getUserNameComment() {
        return userNameComment;
    }
}
