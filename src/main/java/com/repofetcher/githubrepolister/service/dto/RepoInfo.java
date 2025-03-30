package com.repofetcher.githubrepolister.service.dto;

import java.util.List;

public record RepoInfo(String name, String ownerLogin, List<BranchInfo> branches) {}
