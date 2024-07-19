package dev.netanelbcn.kinderkit.Interfaces;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.NewUserBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.UserBoundary;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("/superapp/users")
    Call<UserBoundary> createUser(@Body NewUserBoundary user);

    @PUT("/superapp/users/{superapp}/{email}")
    Call<Void> updateUser(@Path("superapp") String superapp, @Path("email") String email, @Body UserBoundary update);

    @POST("/superapp/objects")
    Call<ObjectBoundary> saveUserData(@Body ObjectBoundary boundaryObject);

    @GET("superapp/users/login/{superapp}/{email}")
    Call<UserBoundary> getUserById(@Path("superapp") String superapp, @Path("email") String email);

    @PUT("/superapp/users/{superapp}/{email}")
    Call<Void> updateParentUser(@Path("superapp") String superapp, @Path("email") String email, @Body UserBoundary update);


    @GET("/superapp/objects/{superapp}/{id}")
    Call<ObjectBoundary> getObjectById(@Path("id") String id,
                                       @Path("superapp") String superapp,
                                       @Query("userSuperapp") String userSuperapp,
                                       @Query("userEmail") String userEmail);

    @PUT("/superapp/objects/{superapp}/{id}")
    Call<Void> updateObject(@Path("id") String id,
                            @Path("superapp") String superapp,
                            @Query("userSuperapp") String userSuperapp,
                            @Query("userEmail") String userEmail,
                            @Body ObjectBoundary objectBoundary);


}
