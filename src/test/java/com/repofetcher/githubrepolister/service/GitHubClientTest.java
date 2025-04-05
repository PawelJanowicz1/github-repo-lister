package com.repofetcher.githubrepolister.service;

import com.repofetcher.githubrepolister.exceptions.UserNotFoundException;
import com.repofetcher.githubrepolister.service.dto.GitHubRepoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GitHubClientTest {

    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private GitHubClient gitHubClient;
    private final String token = "dummy-token";
    @Value("${github.url}")
    private String githubApiUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        gitHubClient = new GitHubClient(restTemplateBuilder, token);
        ReflectionTestUtils.setField(gitHubClient, "GITHUB_API", githubApiUrl);
    }

    @Test
    void fetchUserRepos_ShouldReturnRepos_WhenReposExist() {
        GitHubRepoDto[] repoArray = new GitHubRepoDto[]{
                new GitHubRepoDto("repo1", new GitHubRepoDto.GitHubRepoOwner("owner1"), false)
        };
        ResponseEntity<GitHubRepoDto[]> responseEntity = new ResponseEntity<>(repoArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(GitHubRepoDto[].class))).thenReturn(responseEntity);
        var repos = gitHubClient.fetchUserRepos("someuser");
        assertFalse(repos.isEmpty());
        assertEquals("repo1", repos.get(0).name());
    }

    @Test
    void fetchUserRepos_ShouldThrowException_WhenReposAreEmpty() {
        GitHubRepoDto[] emptyArray = new GitHubRepoDto[]{};
        ResponseEntity<GitHubRepoDto[]> responseEntity = new ResponseEntity<>(emptyArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(GitHubRepoDto[].class))).thenReturn(responseEntity);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            gitHubClient.fetchUserRepos("someuser");
        });
        assertTrue(exception.getMessage().contains("User not found or there are no repositories"));
    }

    @Test
    void fetchUserRepos_ShouldThrowException_WhenNotFound() {
        when(restTemplate.getForEntity(anyString(), eq(GitHubRepoDto[].class)))
                .thenThrow(HttpClientErrorException.create(
                                HttpStatus.NOT_FOUND,
                                "Not Found",
                                new org.springframework.http.HttpHeaders(),
                                new byte[0],
                                java.nio.charset.StandardCharsets.UTF_8
                        ));
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            gitHubClient.fetchUserRepos("nonexistentuser");
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }
}