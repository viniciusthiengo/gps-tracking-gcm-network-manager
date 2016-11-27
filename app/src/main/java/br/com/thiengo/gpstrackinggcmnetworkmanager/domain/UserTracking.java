package br.com.thiengo.gpstrackinggcmnetworkmanager.domain;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface UserTracking {
    @FormUrlEncoded
    @POST("package/ctrl/CtrlUser.php")
    public Call<String> sendCoordinates(
            @Field("method") String method,
            @Field("user_id") String id,
            @Field("latitude") String value,
            @Field("longitude") String token
    );
}
