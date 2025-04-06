package com.repofetcher.githubrepolister.service;

import com.repofetcher.githubrepolister.exceptions.UserNotFoundException;
import com.repofetcher.githubrepolister.service.dto.BranchInfo;
import com.repofetcher.githubrepolister.service.dto.RepoInfo;
import com.repofetcher.githubrepolister.service.dto.GitHubRepoDto;
import com.repofetcher.githubrepolister.service.dto.GitHubBranchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepositoryServiceTest {

    private GitHubClient gitHubClient;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        gitHubClient = Mockito.mock(GitHubClient.class);
        repositoryService = new RepositoryService(gitHubClient);
    }

    @Test
    void getRepositories_ShouldReturnRepoInfoList_WhenReposExist() {
        GitHubRepoDto repoDto = new GitHubRepoDto("repo1", new GitHubRepoDto.GitHubRepoOwner("owner1"), false);
        GitHubBranchDto branchDto = new GitHubBranchDto("branch1", new GitHubBranchDto.GitHubCommit("abc123"));
        when(gitHubClient.fetchUserRepos("testuser")).thenReturn(List.of(repoDto));
        when(gitHubClient.fetchBranches("owner1", "repo1")).thenReturn(List.of(branchDto));
        List<RepoInfo> repos = repositoryService.getRepositories("testuser");
        assertFalse(repos.isEmpty());
        RepoInfo repoInfo = repos.get(0);
        assertEquals("repo1", repoInfo.name());
        assertEquals("owner1", repoInfo.ownerLogin());
        List<BranchInfo> branches = repoInfo.branches();
        assertFalse(branches.isEmpty());
        assertEquals("branch1", branches.get(0).name());
        assertEquals("abc123", branchDto.commit().sha());
    }

    @Test
    void getRepositories_ShouldFilterOutForks() {
        GitHubRepoDto repoNonFork = new GitHubRepoDto("repo1", new GitHubRepoDto.GitHubRepoOwner("owner1"), false);
        GitHubRepoDto repoFork = new GitHubRepoDto("repo2", new GitHubRepoDto.GitHubRepoOwner("owner1"), true);
        when(gitHubClient.fetchUserRepos("testuser")).thenReturn(List.of(repoNonFork, repoFork));
        when(gitHubClient.fetchBranches(anyString(), anyString())).thenReturn(List.of());
        List<RepoInfo> repos = repositoryService.getRepositories("testuser");
        assertEquals(1, repos.size());
        assertEquals("repo1", repos.get(0).name());
    }

    @Test
    void getRepositories_ShouldThrowException_WhenGitHubClientThrowsException() {
        when(gitHubClient.fetchUserRepos("baduser")).thenThrow(new UserNotFoundException("User not found"));
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            repositoryService.getRepositories("baduser");
        });
        assertEquals("User not found", exception.getMessage());
    }
}