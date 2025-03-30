package com.repofetcher.githubrepolister.exceptions.dto;

public record ApiErrorResponse(int status, String message) {}