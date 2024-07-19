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

public interface BabysitterService {


    @GET("/superapp/objects/search/byType/{type}")
    Call<List<ObjectBoundary>> loadAllBabysitters(@Path("type") String type,
                                                  @Query("userSuperapp") String userSuperapp,
                                                  @Query("userEmail") String userEmail
    );

    @POST("/superapp/miniapp/{miniAppName}")
    Call<List<Object>> loadAllBabysittersByDistance(@Path("miniAppName") String miniAppName,
                                                    @Body MiniAppCommandBoundary commandBoundary);
}
