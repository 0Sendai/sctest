package ru.sctest.backend.login;

public sealed interface PostResponse 
    permits PostResponse.Success, PostResponse.Fail 
{
    record Success(String status) implements PostResponse {}
    record Fail(String error) implements PostResponse {}
}
