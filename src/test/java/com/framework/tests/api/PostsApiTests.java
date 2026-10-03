package com.framework.tests.api;

import com.framework.api.ApiClient;
import com.framework.api.models.Post;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * GET / POST / PUT / PATCH / DELETE against JSONPlaceholder (api.base.url).
 * JSONPlaceholder is a fake API: writes return realistic responses but nothing is saved,
 * so each test checks only the response of its own call. See BookingApiTests for a flow that persists.
 */
public class PostsApiTests {

    @Test(groups = {"api", "smoke"}, description = "GET /posts returns all 100 posts quickly")
    public void getAllPosts() {
        Response response = ApiClient.get("/posts");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getContentType().contains("application/json"), "Unexpected content type");
        Assert.assertEquals(response.jsonPath().getList("$").size(), 100, "Unexpected number of posts");
        Assert.assertTrue(response.getTime() < 5000, "Response took " + response.getTime() + " ms");
    }

    @Test(groups = {"api", "smoke"}, description = "GET /posts/{id} returns that post")
    public void getPostById() {
        Response response = ApiClient.get("/posts/1");

        Assert.assertEquals(response.getStatusCode(), 200);
        Post post = response.as(Post.class);
        Assert.assertEquals(post.id(), Integer.valueOf(1));
        Assert.assertEquals(post.userId(), Integer.valueOf(1));
        Assert.assertFalse(post.title().isBlank(), "Title should not be blank");
    }

    @Test(groups = {"api", "regression"}, description = "GET /posts?userId=1 returns only that user's posts")
    public void getPostsFilteredByQueryParam() {
        Response response = ApiClient.request().queryParam("userId", 1).get("/posts");

        Assert.assertEquals(response.getStatusCode(), 200);
        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        Assert.assertEquals(userIds.size(), 10, "Unexpected number of posts for user 1");
        Assert.assertTrue(userIds.stream().allMatch(id -> id == 1), "Found posts of other users: " + userIds);
    }

    @Test(groups = {"api", "regression"}, description = "GET /posts/{id}/comments returns the post's comments")
    public void getCommentsOfPost() {
        Response response = ApiClient.get("/posts/1/comments");

        Assert.assertEquals(response.getStatusCode(), 200);
        List<Integer> postIds = response.jsonPath().getList("postId", Integer.class);
        Assert.assertEquals(postIds.size(), 5, "Unexpected number of comments");
        Assert.assertTrue(postIds.stream().allMatch(id -> id == 1), "Comments of other posts returned");
        Assert.assertTrue(response.jsonPath().getList("email", String.class).stream().allMatch(e -> e.contains("@")),
                "Every comment should have an email");
    }

    @Test(groups = {"api", "regression"}, description = "GET /posts/{id} for an unknown id returns 404")
    public void getUnknownPostReturns404() {
        Response response = ApiClient.get("/posts/9999");

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.asString().trim(), "{}");
    }

    @Test(groups = {"api", "smoke"}, description = "POST /posts creates a post and returns it with a new id")
    public void createPost() {
        Post newPost = new Post(1, null, "Automation title", "Created by Rest Assured");

        Response response = ApiClient.request().body(newPost).post("/posts");

        Assert.assertEquals(response.getStatusCode(), 201);
        Post created = response.as(Post.class);
        Assert.assertEquals(created.id(), Integer.valueOf(101), "New post should get the next id");
        Assert.assertEquals(created.title(), newPost.title());
        Assert.assertEquals(created.body(), newPost.body());
        Assert.assertEquals(created.userId(), newPost.userId());
    }

    @Test(groups = {"api", "regression"}, description = "PUT /posts/{id} replaces the whole post")
    public void updatePost() {
        Post updated = new Post(1, 1, "Updated title", "Updated body");

        Response response = ApiClient.request().body(updated).put("/posts/1");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.as(Post.class), updated);
    }

    @Test(groups = {"api", "regression"}, description = "PATCH /posts/{id} changes only the fields sent")
    public void partiallyUpdatePost() {
        Response response = ApiClient.request().body(Map.of("title", "Patched title")).patch("/posts/1");

        Assert.assertEquals(response.getStatusCode(), 200);
        Post patched = response.as(Post.class);
        Assert.assertEquals(patched.title(), "Patched title");
        Assert.assertEquals(patched.id(), Integer.valueOf(1));
        Assert.assertFalse(patched.body().isBlank(), "Fields not sent should keep their value");
    }

    @Test(groups = {"api", "smoke"}, description = "DELETE /posts/{id} returns 200 and an empty body")
    public void deletePost() {
        Response response = ApiClient.delete("/posts/1");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.asString().trim(), "{}");
    }
}
