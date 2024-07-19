package dev.netanelbcn.kinderkit.Interfaces;


import java.util.List;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.MiniAppCommandBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {


    @POST("/superapp/objects")
    Call<ObjectBoundary> createEvent(@Body ObjectBoundary boundaryObject);

    @GET("/superapp/objects/search/byType/{type}")
    Call<List<ObjectBoundary>> loadAllBabysittingEvents(@Path("type") String type,
                                                  @Query("userSuperapp") String userSuperapp,
                                                  @Query("userEmail") String userEmail
    );


}
