package com.audition.integration;

import com.audition.common.ApplicationProperties;
import com.audition.common.exception.ItemNotFoundException;
import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuditionIntegrationClient implements AuditionService {

    @Autowired
    AuditionLogger logger;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationProperties properties;

    public List<AuditionPost> getPosts(String title, String body) {
        try {
            List<AuditionPost> posts = restTemplate.exchange(properties.getEndpoint().getPostUrl(), HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<AuditionPost>>() {
                    }).getBody();
            return posts.stream().filter(Objects::nonNull).filter(post -> post.getBody().equalsIgnoreCase(body)
                    || post.getTitle().equalsIgnoreCase(title)).toList();
        } catch (Exception ex) {
            logger.error(log, ex.getMessage());
            throw new SystemException(ex.getMessage(), 500, ex);
        }
    }

    public AuditionPost getPostById(final String id) {
        try {
            AuditionPost post = restTemplate.getForEntity(properties.getEndpoint().getPostUrl()+"/"+id, AuditionPost.class).getBody();
            return Optional.ofNullable(post).orElseThrow(() -> new ItemNotFoundException(
                    String.format("Cannot find a Post with id %s Resource Not Found", id)));
        } catch (final Exception ex) {
            logger.error(log, ex.getMessage());
            throw new SystemException(ex.getMessage(), 500, ex);
        }
    }

    public List<Comment> getCommentsOnPost(final String postId) {
        try {
            String commentsUrl = String.format("%s/%s/comments", properties.getEndpoint().getPostUrl(), postId);
            List<Comment> comments =  restTemplate.exchange(commentsUrl, HttpMethod.GET,
            null, new ParameterizedTypeReference<List<Comment>>() {
            }).getBody();
            return Optional.ofNullable(comments).orElseThrow(() -> new ItemNotFoundException(
                    String.format("Cannot find comments with id %s Resource Not Found", postId)));
        } catch (final Exception ex) {
            logger.error(log, ex.getMessage());
            throw new SystemException(ex.getMessage(), 500, ex);
        }
    }

    public List<Comment> getComments(final String postId) {
        try {
            String commentsUrl = String.format("%s?postId=%s", properties.getEndpoint().getCommentsUrl(),
                    postId);
            List<Comment> comments = restTemplate.exchange(commentsUrl, HttpMethod.GET,
            null, new ParameterizedTypeReference<List<Comment>>() {
            }).getBody();
            return Optional.ofNullable(comments).orElseThrow(() -> new ItemNotFoundException(
                    String.format("Cannot find comments with id %s Resource Not Found", postId)));
        } catch (final Exception ex) {
            logger.error(log, ex.getMessage());
            throw new SystemException(ex.getMessage(), 500, ex);
        }
    }
   
}
