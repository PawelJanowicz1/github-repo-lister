package com.repofetcher.githubrepolister.service;

import com.repofetcher.githubrepolister.service.dto.BranchInfo;
import com.repofetcher.githubrepolister.service.dto.RepoInfo;
import com.repofetcher.githubrepolister.service.dto.GitHubRepoDto;
import com.repofetcher.githubrepolister.service.dto.GitHubBranchDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RepositoryService {

    private final GitHubClient gitHubClient;

    public RepositoryService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<RepoInfo> getRepositories(String username) {
        return gitHubClient.fetchUserRepos(username)
                .stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    String repoName = repo.name();
                    String ownerLogin = repo.owner().login();
                    List<GitHubBranchDto> branches = gitHubClient.fetchBranches(ownerLogin, repoName);
                    List<BranchInfo> branchInfos = branches.stream()
                            .map(br -> new BranchInfo(br.name(), br.commit().sha()))
                            .toList();
                    return new RepoInfo(repoName, ownerLogin, branchInfos);
                })
                .toList();
    }
}