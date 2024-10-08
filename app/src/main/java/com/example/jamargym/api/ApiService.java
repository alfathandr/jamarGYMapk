package com.example.jamargym.api;

import com.example.jamargym.request.RegisterRequest;
import com.example.jamargym.response.RegisterResponse;
import com.example.jamargym.request.LoginRequest;
import com.example.jamargym.response.LoginResponse;
import com.example.jamargym.response.PackageResponse;
import com.example.jamargym.response.UserResponse;
import com.example.jamargym.response.BrandResponse;
import com.example.jamargym.request.UserUpdateRequest;
import com.example.jamargym.response.UserResponse;
import com.example.jamargym.response.OrderResponse;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.http.Part;
import retrofit2.http.Header;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Multipart;

public interface ApiService {
    @POST("users/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("packages")
    Call<PackageResponse> getPackages(@Header("Authorization") String authToken);

    @GET("users/me")
    Call<UserResponse> getUserData(@Header("Authorization") String authToken);

    @GET("brand")
    Call<BrandResponse> getBrandData();

    @PUT("users/update")
    Call<UserResponse> updateUser(
            @Header("Authorization") String authToken,
            @Body UserUpdateRequest userUpdateRequest
    );

    @Multipart
    @POST("users/order")
    Call<OrderResponse> placeOrder(
            @Header("Authorization") String authToken,
            @Part("package_id") RequestBody packageId,
            @Part MultipartBody.Part image
    );


}
