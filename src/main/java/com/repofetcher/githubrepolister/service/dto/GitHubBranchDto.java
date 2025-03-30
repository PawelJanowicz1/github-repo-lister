package com.repofetcher.githubrepolister.service.dto;

import com.repofetcher.githubrepolister.service.GitHubClient;

public record GitHubBranchDto(String name, GitHubCommit commit) {
    public static record GitHubCommit(String sha) {
    }
}
