package com.audition.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;

@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionPost>> getPost(@RequestParam(required = false) String title, @RequestParam(required = false) String body) {
        return ResponseEntity.ok(auditionService.getPosts(title, body));
    }

    @GetMapping(value = "/posts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditionPost> getPostById(@PathVariable("id") final String postId) {
        final AuditionPost auditionPosts = auditionService.getPostById(postId);
        return ResponseEntity.ok(auditionPosts);
    }
    @GetMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getComments(@RequestParam(required = false) String postId) {
        final List<Comment> comments = auditionService.getComments(postId);
        return ResponseEntity.ok(comments);
    }
    @GetMapping(value = "/posts/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getCommentsOnPost(@PathVariable("id") final String postId) {
        final List<Comment> comments = auditionService.getCommentsOnPost(postId);
        return ResponseEntity.ok(comments);
    }
    
}
