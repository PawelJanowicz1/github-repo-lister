package com.repofetcher.githubrepolister.service.dto;

import com.repofetcher.githubrepolister.service.GitHubClient;

public record GitHubRepoDto(String name, GitHubRepoOwner owner, boolean fork) {
    public static record GitHubRepoOwner(String login) {
    }
}
