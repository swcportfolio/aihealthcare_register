package com.lukken.aihealthcareregister.HttpTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface HttpTask {
    String URL_DOMAIN = "http://lifelogop.ghealth.or.kr";
    String URL_BIODATA = URL_DOMAIN + "/aihealthcare/biodata/";

    /**
     * 신규가입 유저키 생성
     */
    @GET("ws/ucode/gen")
    Call<JsonObject> getNewUCode();


    /**
     * 가입가능한 번호인지 체크
     * @param mobile 휴대폰 번호
     * @return
     */
    @GET("ws/mobile/check")
    Call<JsonObject> getCheckMobile(@Query("mobile") String mobile);


    /**
     * 회원가입
     * @param param
     * @param biodata
     * @return
     */
    @Multipart
    @POST("ws/account/create")
    Call<JsonObject> postCreateAccount(@PartMap HashMap<String, RequestBody> param, @Part MultipartBody.Part biodata);


    /**
     * 회원 정보
     * @param ucode
     * @return
     */
    @GET("ws/userinfo")
    Call<JsonObject> getGHealth(@Query("ucode") String ucode);


    /**
     * 얼굴정보 데이터 리스트
     * @return
     */
    @GET("ws/biodata/get")
    Call<JsonArray> getBioDataList();

    /**
     * 키오스크 소켓정보
     * @return
     */
    @GET("ws/sockinfo/get")
    Call<JsonObject> getSocketInfo();

}
