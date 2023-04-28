import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

public class PlaceholderTest {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Test
    public void testApi(){
        // Generate a random user ID
        int userId = new Random().nextInt(10) + 1; // User IDs are between 1 and 10
        System.out.println("Selected User ID: " + userId);

        // Get the user's email address
        Response userResponse = RestAssured.given()
                .baseUri(BASE_URL)
                .basePath("/users/" + userId)
                .when()
                .get();
        userResponse.then().statusCode(200);
        String email = userResponse.jsonPath().getString("email");
        System.out.println("User's email address: " + email);


        // Get the user's posts and verify the validity of Post IDs
        Response userPostsResponse = RestAssured.given()
                .baseUri(BASE_URL)
                .basePath("/posts/"+ userId)
                .when()
                .get();
        userPostsResponse.then().statusCode(200);
        int[] postIds = userPostsResponse.jsonPath().get("id");
        for (int postId : postIds) {
            Assert.assertTrue(postId >= 1 && postId <= 100, "Post ID " + postId + " is not valid for the user");
        }

        // Create a new post using this userID with a title and body
        Response newPostResponse = RestAssured.given()
                .baseUri(BASE_URL)
                .basePath("/posts")
                .body("{\"userId\": " + userId + ", \"title\": \"New post title\", \"body\": \"New post body\"}")
                .when()
                .post();
        newPostResponse.then().statusCode(201);
        int newPostId = newPostResponse.jsonPath().getInt("id");
        Assert.assertEquals(newPostResponse.jsonPath().getString("title"), "New post title");
        Assert.assertEquals(newPostResponse.jsonPath().getString("body"), "New post body");
        Assert.assertEquals(newPostResponse.jsonPath().getInt("userId"), userId);
        System.out.println("New post created with ID: " + newPostId);
    }



}
