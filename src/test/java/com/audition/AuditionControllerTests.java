package com.audition;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.web.AuditionController;
import com.audition.web.advice.ExceptionControllerAdvice;

class AuditionControllerTests {

    private MockMvc mockMvc;
    @Mock
    private AuditionIntegrationClient client;

    @InjectMocks
    AuditionController auditionApi;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(auditionApi)
        .setControllerAdvice(new ExceptionControllerAdvice())
        .build();
    }

    @Test
    void getPosts_shouldReturnOk() throws Exception {
        when(client.getPosts("title", "body")).thenReturn(List.of(AuditionPost.builder().id(1).body("body").title("title").build()));
        mockMvc.perform(get("/posts?title=title&body=body"))
                .andExpect(status().isOk());
    }

    @Test
    void getPostByPostId_shouldReturnOk() throws Exception {
        String postId = "1";
        when(client.getPostById(postId)).thenReturn(null);
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentsByPostId_shouldReturnOk() throws Exception {
        when(client.getComments("postId")).thenReturn(null);
        mockMvc.perform(get("/comments?postId=1"))
                .andExpect(status().isOk());
    }
    @Test
    void getPosts_shouldReturnInternalServerError() throws Exception {
        when(client.getPosts("title", "body")).thenThrow(new SystemException("System Exception"));
        mockMvc.perform(get("/posts?title=title&body=body"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPostByPostId_shouldReturnInternalServerError() throws Exception {
        String postId = "1";
        when(client.getPostById(postId)).thenThrow(new SystemException("System Exception"));
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCommentsByPostId_shouldReturnInternalServerError() throws Exception {
        when(client.getComments("postId")).thenThrow(new SystemException("System Exception"));
        mockMvc.perform(get("/comments?postId=postId"))
                .andExpect(status().isInternalServerError());
    }

}
