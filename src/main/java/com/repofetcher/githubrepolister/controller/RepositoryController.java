package com.repofetcher.githubrepolister.controller;

import com.repofetcher.githubrepolister.service.dto.RepoInfo;
import com.repofetcher.githubrepolister.service.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class RepositoryController {

    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/{username}/repos")
    public List<RepoInfo> listUserRepositories(@PathVariable String username) {
        return repositoryService.getRepositories(username);
    }
}
