package com.buaa.food;

public class CommentPreview implements Comparable<CommentPreview> {
    int dishId;
    int commentId;
    int userId;
    String comment;
    String time;

    public CommentPreview(int dishId, int userId, int commentId, String comment, String time) {
        this.dishId = dishId;
        this.userId = userId;
        this.commentId = commentId;
        this.comment = comment;
        this.time = time;
    }

    public int getDishId() {
        return dishId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCommentId() {
        return commentId;
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
