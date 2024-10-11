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

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.web.AuditionController;

class AuditionApplicationTests {

    // TODO implement unit test. Note that an applicant should create additional
    // unit tests as required.

    private MockMvc mockMvc;
    @Mock
    private AuditionIntegrationClient client;

    @InjectMocks
    AuditionController auditionApi;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(auditionApi)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getPosts_shouldReturnOk() throws Exception {
        when(client.getPosts("title", "body")).thenReturn(List.of(AuditionPost.builder().id(1).body("body").title("title").build()));
        mockMvc.perform(get("/posts?title=title&body=body"))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentsByPostId_shouldReturnOk() throws Exception {
        String postId = "1";
        // when(applicationProperties.getEndpoint().getCommentsUrl())
        // .thenReturn("https://jsonplaceholder.typicode.com/comments?postId=");
        when(client.getPostById(postId)).thenReturn(null);
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk());
    }

}
