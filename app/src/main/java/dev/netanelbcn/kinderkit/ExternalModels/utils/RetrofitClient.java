package dev.netanelbcn.kinderkit.ExternalModels.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //private static final String BASE_URL = "http://10.0.2.2:8084"; //emulator
    //private static final String BASE_URL = "http://192.168.1.140:8084";  //device at home
    // private static final String BASE_URL = "http://172.20.10.3:8084";  //device at Afeka
    //private static final String BASE_URL = "http://192.168.1.84:8084";  //device at home
    private static final String BASE_URL = "http://172.20.10.3:8084";  //device at Afeka


    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        if (instance == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
            this.retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Retrofit getClient() {
        return this.retrofit;
    }
}
