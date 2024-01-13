package com.buaa.food;

public class CommentPreview implements Comparable<CommentPreview> {
    int dishId;
    int commentId;
    String userName;
    String comment;
    String time;

    public CommentPreview(int dishId, int commentId, String userName, String comment, String time) {
        this.dishId = dishId;
        this.commentId = commentId;
        this.userName = userName;
        this.comment = comment;
        this.time = time;
    }

    public int getDishId() {
        return dishId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getUserName() {
        return userName;
    }


    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int compareTo(CommentPreview other) {
        return Integer.compare(this.commentId, other.commentId);
    }
}
