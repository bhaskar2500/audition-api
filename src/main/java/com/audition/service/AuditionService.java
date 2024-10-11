package com.audition.service;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;

import java.util.List;

public interface AuditionService {
    public List<AuditionPost> getPosts(String title, String body);
    public AuditionPost getPostById(final String postId);
    public List<Comment> getCommentsOnPost(final String postId) ;
    public  List<Comment> getComments(final String postId) ;
}
