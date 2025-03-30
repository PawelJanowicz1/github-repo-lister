package com.repofetcher.githubrepolister.service;
import com.repofetcher.githubrepolister.service.dto.GitHubBranchDto;
import com.repofetcher.githubrepolister.service.dto.GitHubRepoDto;
import com.repofetcher.githubrepolister.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitHubClient {

    private final RestTemplate restTemplate;
    private static final String GITHUB_API = "https://api.github.com";

    public GitHubClient(RestTemplateBuilder restTemplateBuilder,
                        @Value("${github.token}") String token) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Accept", "application/vnd.github+json");
            request.getHeaders().set("User-Agent", "GithubRepoLister");
            request.getHeaders().set("Authorization", "token " + token);
            return execution.execute(request, body);
        });
    }

    public List<GitHubRepoDto> fetchUserRepos(String username) {
        String userUrl = GITHUB_API + "/users/" + username;
        try {
            restTemplate.getForEntity(userUrl, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found");
        }

        String reposUrl = GITHUB_API + "/users/" + username + "/repos";
        try {
            ResponseEntity<GitHubRepoDto[]> response = restTemplate.getForEntity(reposUrl, GitHubRepoDto[].class);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            }
            GitHubRepoDto[] reposArray = response.getBody();
            if (reposArray == null) {
                return List.of();
            }
            return List.of(reposArray);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found");
        }
    }

    public List<GitHubBranchDto> fetchBranches(String owner, String repo) {
        String url = GITHUB_API + "/repos/" + owner + "/" + repo + "/branches";
        try {
            ResponseEntity<GitHubBranchDto[]> response = restTemplate.getForEntity(url, GitHubBranchDto[].class);
            GitHubBranchDto[] branchesArray = response.getBody();
            if (branchesArray == null) {
                return List.of();
            }
            return List.of(branchesArray);
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        }
    }
}