package com.audition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.audition.common.ApplicationProperties;
import com.audition.common.ApplicationProperties.Endpoint;
import com.audition.common.exception.ItemNotFoundException;
import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AuditionIntegrationClientTests {

    @InjectMocks
    @Spy
    private AuditionIntegrationClient auditionIntegrationClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    Endpoint endpoint;
    
    @Mock
    AuditionLogger logger;

    @Autowired
    private ApplicationProperties properties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        endpoint = new Endpoint("https://jsonplaceholder.typicode.com/posts", "https://jsonplaceholder.typicode.com/comments");
        properties = new ApplicationProperties(endpoint);
        ReflectionTestUtils.setField(auditionIntegrationClient, "properties", properties, ApplicationProperties.class);
    }

    @Test
    void testGetPostsWithFilters() {
        String title = "testTitle";
        String body = "testBody";
        List<AuditionPost> posts = new ArrayList<>();
        posts.add(new AuditionPost(1, 1, title, body));
        posts.add(new AuditionPost(2, 2, "title2", " body2"));
        ResponseEntity<List<AuditionPost>> responseEntity = new ResponseEntity<>(posts, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(),
                eq(new ParameterizedTypeReference<List<AuditionPost>>() {
                }))).thenReturn(responseEntity);
        List<AuditionPost> result = auditionIntegrationClient.getPosts(title, body);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(body, result.get(0).getBody());
    }

    @Test
    void testGetPostsWithException() {
        when(restTemplate.exchange(anyString(), any(), any(),
                eq(new ParameterizedTypeReference<List<AuditionPost>>() {
                }))).thenThrow(new RuntimeException("Test Exception"));
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts("title", "body"));
    }

    @Test
    void testGetPostByIdSuccess() {
        // Arrange
        int postId = 1;
        AuditionPost post = new AuditionPost(postId, 1, "title", "body");
        ResponseEntity<AuditionPost> responseEntity = new ResponseEntity<>(post, HttpStatus.OK);
        when(restTemplate.getForEntity("https://jsonplaceholder.typicode.com/posts/" + postId, AuditionPost.class))
                .thenReturn(responseEntity);
        AuditionPost result = auditionIntegrationClient.getPostById(String.valueOf(postId));
        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void testGetPostByIdNotFound() {
        int postId = 1;
        ResponseEntity<AuditionPost> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.getForEntity("https://jsonplaceholder.typicode.com/" + postId, AuditionPost.class))
                .thenReturn(responseEntity);
        assertThrows(SystemException.class,
                () -> auditionIntegrationClient.getPostById(String.valueOf(postId)));
    }

    @Test
    void testGetPostByIdWithException() {
        int postId = 1;
        when(restTemplate.getForEntity("https://jsonplaceholder.typicode.com/" + postId, AuditionPost.class))
                .thenThrow(new RuntimeException("Test Exception"));
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("1"));
    }

    @Test
    void testGetCommentsOnPostSuccess() {
        int postId = 1;
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1, 1, "name", "email", "body"));
        ResponseEntity<List<Comment>> responseEntity = new ResponseEntity<>(comments, HttpStatus.OK);
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenReturn(responseEntity);
        List<Comment> result = auditionIntegrationClient.getCommentsOnPost(String.valueOf(postId));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCommentsOnPostNotFound() {
        int postId = 1;
        ResponseEntity<List<Comment>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/" + postId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenReturn(responseEntity);
        assertThrows(SystemException.class,
                () -> auditionIntegrationClient.getCommentsOnPost(String.valueOf(postId)));
    }

    @Test
    void testGetCommentsOnPostWithException() {
        int postId = 1;
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/" + postId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(SystemException.class,
                () -> auditionIntegrationClient.getCommentsOnPost(String.valueOf(postId)));
    }

    @Test
    void testGetCommentsSuccess() {
        String postId = "1";
        List<Comment> comments = Collections.singletonList(new Comment(1, 1, "name", "email", "body"));
        ResponseEntity<List<Comment>> responseEntity = new ResponseEntity<>(comments, HttpStatus.OK);
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/comments?postId=" + postId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenReturn(responseEntity);

        List<Comment> result = auditionIntegrationClient.getComments(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCommentsNotFound() {
        String postId = "1";
        ResponseEntity<List<Comment>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/comments?postId=" + postId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenReturn(responseEntity);
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getComments(postId));
    }

    @Test
    void testGetCommentsWithException() {
        String postId = "1";
        when(restTemplate.exchange(
                "https://jsonplaceholder.typicode.com/comments?postId=" + postId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                })).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(SystemException.class, () -> auditionIntegrationClient.getComments(postId));
    }
}
