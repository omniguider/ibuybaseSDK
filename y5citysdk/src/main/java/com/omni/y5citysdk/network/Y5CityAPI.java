package com.omni.y5citysdk.network;

import android.app.Activity;
import android.util.Log;

import com.omni.y5citysdk.module.CommonArrayResponse;
import com.omni.y5citysdk.module.CommonResponse;
import com.omni.y5citysdk.module.favorite.FavoriteData;
import com.omni.y5citysdk.module.favorite.FavoriteResponse;
import com.omni.y5citysdk.module.geo_fence.GeoFenceResponse;
import com.omni.y5citysdk.module.info.BannerData;
import com.omni.y5citysdk.module.info.CoverData;
import com.omni.y5citysdk.module.info.InfoData;
import com.omni.y5citysdk.module.info.InstructionData;
import com.omni.y5citysdk.module.login.CheckUserLoginData;
import com.omni.y5citysdk.module.login.LogoutResponseData;
import com.omni.y5citysdk.module.point.AreaData;
import com.omni.y5citysdk.module.point.ObjectiveData;
import com.omni.y5citysdk.module.point.PointData;
import com.omni.y5citysdk.module.project.ProjectData;
import com.omni.y5citysdk.module.trip.MasterTripData;
import com.omni.y5citysdk.module.trip.PointInfoFeedback;
import com.omni.y5citysdk.module.trip.ReligionInfoFeedback;
import com.omni.y5citysdk.module.trip.ReligionListData;
import com.omni.y5citysdk.module.trip.ThemeFeedback;
import com.omni.y5citysdk.module.trip.TripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripData;
import com.omni.y5citysdk.module.trip.UserTripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripUpdateData;
import com.omni.y5citysdk.tool.DialogTools;
import com.omni.y5citysdk.tool.Tools;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class Y5CityAPI {

    private static Y5CityAPI mY5CityAPI;

    public static Y5CityAPI getInstance() {
        if (mY5CityAPI == null) {
            mY5CityAPI = new Y5CityAPI();
        }
        return mY5CityAPI;
    }

    interface Y5CityService {
        @FormUrlEncoded
        @POST("api/get_banner")
        Call<CommonArrayResponse> getBanner(@Field("timestamp") String timestamp,
                                            @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_cover")
        Call<CommonArrayResponse> getCover(@Field("timestamp") String timestamp,
                                           @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_info")
        Call<CommonArrayResponse> getInfo(@Field("timestamp") String timestamp,
                                          @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_intruction")
        Call<CommonArrayResponse> getInstruction(@Field("timestamp") String timestamp,
                                                 @Field("mac") String mac);


        @FormUrlEncoded
        @POST("api/get_official_trip")
        Call<CommonArrayResponse> getOfficialTrip(@Field("lang") String lang,
                                                  @Field("timestamp") String timestamp,
                                                  @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_trip_info")
        Call<CommonResponse> getTripInfo(@Field("t_id") String t_id,
                                         @Field("timestamp") String timestamp,
                                         @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_religion_info")
        Call<CommonResponse> getReligionInfo(@Field("p_id") String p_id,
                                             @Field("timestamp") String timestamp,
                                             @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_point_info")
        Call<CommonResponse> getPointInfo(@Field("p_id") String p_id,
                                          @Field("timestamp") String timestamp,
                                          @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/fb_login")
        Call<CommonResponse> loginWithFB(@Field("parameter") String parameter,
                                         @Field("device_id") String deviceId,
                                         @Field("timestamp") String timestamp,
                                         @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/google_login")
        Call<CommonResponse> loginWithGoogle(@Field("parameter") String parameter,
                                             @Field("device_id") String deviceId,
                                             @Field("timestamp") String timestamp,
                                             @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/logout")
        Call<CommonResponse> logout(@Field("login_token") String login_token,
                                    @Field("device_id") String deviceId,
                                    @Field("timestamp") String timestamp,
                                    @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip")
        Call<CommonArrayResponse> getUserTrip(@Field("device_id") String deviceId,
                                              @Field("login_token") String login_token,
                                              @Field("timestamp") String timestamp,
                                              @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_master_trips")
        Call<CommonArrayResponse> getMasterTrips(@Field("device_id") String deviceId,
                                                 @Field("login_token") String login_token,
                                                 @Field("timestamp") String timestamp,
                                                 @Field("mac") String mac);


        @FormUrlEncoded
        @POST("api/user_trip_info")
        Call<CommonResponse> getUserTripInfo(@Field("t_id") String t_id,
                                             @Field("timestamp") String timestamp,
                                             @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip_copy")
        Call<CommonResponse> userTripCopy(@Field("device_id") String deviceId,
                                          @Field("login_token") String login_token,
                                          @Field("t_id") String t_id,
                                          @Field("title") String title,
                                          @Field("start_time") String start_time,
                                          @Field("timestamp") String timestamp,
                                          @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip_create")
        Call<CommonResponse> userTripCreate(@Field("device_id") String deviceId,
                                            @Field("login_token") String login_token,
                                            @Field("title") String title,
                                            @Field("traffic_tool") String traffic_tool,
                                            @Field("timestamp") String timestamp,
                                            @Field("mac") String mac);


        @FormUrlEncoded
        @POST("api/user_trip_update")
        Call<CommonResponse> userTripUpdate(@Field("device_id") String deviceId,
                                            @Field("login_token") String login_token,
                                            @Field("t_id") String t_id,
                                            @Field("title") String title,
                                            @Field("start_date") String start_date,
                                            @Field("start_time") String start_time,
                                            @Field("timestamp") String timestamp,
                                            @Field("mac") String mac);


        @FormUrlEncoded
        @POST("api/user_trip_point")
        Call<CommonResponse> userTripPoint(@Field("device_id") String deviceId,
                                           @Field("login_token") String login_token,
                                           @Field("t_id") String t_id,
                                           @Field("points") String points,
                                           @Field("timestamp") String timestamp,
                                           @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip_point_once")
        Call<CommonResponse> userTripPointOnce(@Field("device_id") String deviceId,
                                               @Field("login_token") String login_token,
                                               @Field("t_id") String t_id,
                                               @Field("points") String points,
                                               @Field("timestamp") String timestamp,
                                               @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip_delete")
        Call<CommonArrayResponse> userTripDelete(@Field("device_id") String deviceId,
                                                 @Field("login_token") String login_token,
                                                 @Field("t_id") String t_id,
                                                 @Field("timestamp") String timestamp,
                                                 @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/user_trip_share")
        Call<CommonArrayResponse> userTripShare(@Field("device_id") String deviceId,
                                                @Field("login_token") String login_token,
                                                @Field("t_id") String t_id,
                                                @Field("share") String share,
                                                @Field("timestamp") String timestamp,
                                                @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_area")
        Call<CommonArrayResponse> getArea(@Field("type") String type,
                                          @Field("timestamp") String timestamp,
                                          @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_objective")
        Call<CommonArrayResponse> getObjective(@Field("timestamp") String timestamp,
                                               @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_religion_list")
        Call<CommonArrayResponse> getReligionList(@Field("a_id") String a_id,
                                                  @Field("ro_id") String ro_id,
                                                  @Field("timestamp") String timestamp,
                                                  @Field("mac") String mac);


        @FormUrlEncoded
        @POST("api/favorite")
        Call<FavoriteResponse> setFavorite(@Field("p_id") String p_id,
                                           @Field("device_id") String deviceId,
                                           @Field("login_token") String login_token,
                                           @Field("timestamp") String timestamp,
                                           @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/favorite")
        Call<FavoriteResponse> setFavoriteTrip(@Field("t_id") String t_id,
                                               @Field("device_id") String deviceId,
                                               @Field("login_token") String login_token,
                                               @Field("timestamp") String timestamp,
                                               @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_favorite")
        Call<CommonResponse> getFavorite(@Field("device_id") String deviceId,
                                         @Field("login_token") String login_token,
                                         @Field("timestamp") String timestamp,
                                         @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_points")
        Call<CommonResponse> getPoints(@Field("type") String type,
                                       @Field("a_id") String a_id,
                                       @Field("keyword") String keyword,
                                       @Field("user_lat") String user_lat,
                                       @Field("user_lng") String user_lng,
                                       @Field("range") String range,
                                       @Field("device_id") String deviceId,
                                       @Field("login_token") String login_token,
                                       @Field("timestamp") String timestamp,
                                       @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_beacon")
        Call<CommonArrayResponse> getBeaconsPushInfo(@Field("minor") String minors,
                                                     @Field("device_id") String deviceId,
                                                     @Field("login_token") String loginToken,
                                                     @Field("timestamp") String timestamp,
                                                     @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_geofence")
        Call<CommonArrayResponse> getGeoFence(@Field("id") String geoFenceId,
                                              @Field("lat") double userLat,
                                              @Field("lng") double userLng,
                                              @Field("data") String data,
                                              @Field("device_id") String deviceId,
                                              @Field("login_token") String loginToken,
                                              @Field("timestamp") String timestamp,
                                              @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_fcm_pushed")
        Call<CommonArrayResponse> getFcmPushed(@Field("timestamp") String timestamp,
                                               @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/check_login")
        Call<CommonArrayResponse> checkLogin(@Field("login_token") String loginToken,
                                             @Field("device_id") String deviceId,
                                             @Field("reg_id") String firebaseTokenId,
                                             @Field("timestamp") String timestamp,
                                             @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/sdk_login")
        Call<CommonResponse> sdkLogin(@Field("parameter") String parameter,
                                      @Field("device_id") String deviceId,
                                      @Field("timestamp") String timestamp,
                                      @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_religion_shake")
        Call<CommonArrayResponse> getReligionShake(@Field("category") String category,
                                                   @Field("timestamp") String timestamp,
                                                   @Field("mac") String mac);

        @Multipart
        @POST("api/user_trip_cover")
        Call<CommonArrayResponse> userTripCoverCustom(@Part("device_id") RequestBody deviceId,
                                                      @Part("login_token") RequestBody login_token,
                                                      @Part("t_id") RequestBody t_id,
                                                      @Part MultipartBody.Part trip_cover,
                                                      @Part("timestamp") RequestBody timestamp,
                                                      @Part("mac") RequestBody mac);

        @FormUrlEncoded
        @POST("api/user_trip_cover")
        Call<CommonArrayResponse> userTripCover(@Field("device_id") String deviceId,
                                                @Field("login_token") String login_token,
                                                @Field("t_id") String t_id,
                                                @Field("c_id") String c_id,
                                                @Field("timestamp") String timestamp,
                                                @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/get_project")
        Call<CommonArrayResponse> getProject(@Field("timestamp") String timestamp,
                                             @Field("mac") String mac);

    }

    private Y5CityService getY5CityService() {
        return NetworkManager.getInstance().getRetrofit().create(Y5CityService.class);
    }

    public void getBanner(Activity activity, NetworkManager.NetworkManagerListener<BannerData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getBanner(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, BannerData[].class, listener);
    }

    public void getInfo(Activity activity, NetworkManager.NetworkManagerListener<InfoData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getInfo(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, InfoData[].class, listener);
    }

    public void getInstruction(Activity activity, NetworkManager.NetworkManagerListener<InstructionData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getInstruction(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, InstructionData[].class, listener);
    }

    public void getCover(Activity activity, NetworkManager.NetworkManagerListener<CoverData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getCover(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, CoverData[].class, listener);
    }

    public void getOfficialTrip(Activity activity, String lang, NetworkManager.NetworkManagerListener<ThemeFeedback[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getOfficialTrip(lang, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, ThemeFeedback[].class, listener);
    }

    public void getTripInfo(Activity activity, String tripId, NetworkManager.NetworkManagerListener<TripInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getTripInfo(tripId, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, TripInfoFeedback.class, listener);
    }

    public void getReligionInfo(Activity activity, String regionId, NetworkManager.NetworkManagerListener<ReligionInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getReligionInfo(regionId, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, ReligionInfoFeedback.class, listener);
    }

    public void getPointInfo(Activity activity, String pointId, NetworkManager.NetworkManagerListener<PointInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getPointInfo(pointId, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, PointInfoFeedback.class, listener);
    }

    public void logout(Activity activity, String loginToken, NetworkManager.NetworkManagerListener<LogoutResponseData> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().logout(loginToken, Tools.getInstance().getDeviceId(activity), currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, LogoutResponseData.class, listener);
    }

    public void getUserTrip(Activity activity, String loginToken, NetworkManager.NetworkManagerListener<UserTripData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        Log.e(LOG_TAG, "getDeviceId" + Tools.getInstance().getDeviceId(activity));
        Log.e(LOG_TAG, "loginToken" + loginToken);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getUserTrip(Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, UserTripData[].class, listener);
    }

    public void getMasterTrips(Activity activity, String loginToken, NetworkManager.NetworkManagerListener<MasterTripData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getMasterTrips(Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, MasterTripData[].class, listener);
    }

    public void getUserTripInfo(Activity activity, String t_id, NetworkManager.NetworkManagerListener<UserTripInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getUserTripInfo(t_id, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripInfoFeedback.class, listener);
    }

    public void userTripCopy(Activity activity, String loginToken, String t_id, String title, NetworkManager.NetworkManagerListener<UserTripInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().userTripCopy(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, title, "09:00:00", currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripInfoFeedback.class, listener);
    }

    public void userTripCreate(Activity activity, String loginToken, String title, String traffic_tool, NetworkManager.NetworkManagerListener<UserTripUpdateData> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().userTripCreate(Tools.getInstance().getDeviceId(activity), loginToken,
                title, traffic_tool, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripUpdateData.class, listener);
    }

    public void userTripUpdate(Activity activity, String loginToken, String t_id, String title, String start_date,
                               String start_time, NetworkManager.NetworkManagerListener<UserTripUpdateData> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().userTripUpdate(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, title, start_date, start_time, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripUpdateData.class, listener);
    }

    public void userTripPoint(Activity activity, String loginToken, String t_id, String list, NetworkManager.NetworkManagerListener<UserTripInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

//        String jsonStr = NetworkManager.getInstance().getGson().toJson(list);
//        Log.e(LOG_TAG, "jsonStr" + jsonStr);
        Log.e(LOG_TAG, "list" + list);
        Call<CommonResponse> call = getY5CityService().userTripPoint(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, "[" + list + "]", currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripInfoFeedback.class, listener);
    }

    public void userTripPointOnce(Activity activity, String loginToken, String t_id, String list, NetworkManager.NetworkManagerListener<UserTripInfoFeedback> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().userTripPointOnce(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, "[" + list + "]", currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, UserTripInfoFeedback.class, listener);
    }

    public void userTripDelete(Activity activity, String loginToken, String t_id, NetworkManager.NetworkManagerListener<UserTripUpdateData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().userTripDelete(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, UserTripUpdateData[].class, listener);
    }

    public void userTripShare(Activity activity, String loginToken, String t_id, String share, NetworkManager.NetworkManagerListener<UserTripUpdateData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().userTripShare(Tools.getInstance().getDeviceId(activity), loginToken,
                t_id, share, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, UserTripUpdateData[].class, listener);
    }

    public void getReligionList(Activity activity, String a_id, String ro_id, NetworkManager.NetworkManagerListener<ReligionListData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getReligionList(a_id, ro_id, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, ReligionListData[].class, listener);
    }

    public void getArea(Activity activity, String type, NetworkManager.NetworkManagerListener<AreaData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getArea(type, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, AreaData[].class, listener);
    }

    public void getObjective(Activity activity, NetworkManager.NetworkManagerListener<ObjectiveData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getObjective(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, ObjectiveData[].class, listener);
    }

    public void setFavorite(Activity activity, String p_id, String loginToken, NetworkManager.NetworkManagerListener<FavoriteResponse> listener) {

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<FavoriteResponse> call = getY5CityService().setFavorite(p_id, Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequest(activity, call, FavoriteResponse.class, listener);
    }

    public void setFavoriteTrip(Activity activity, String t_id, String loginToken, NetworkManager.NetworkManagerListener<FavoriteResponse> listener) {

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<FavoriteResponse> call = getY5CityService().setFavoriteTrip(t_id, Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequest(activity, call, FavoriteResponse.class, listener);
    }

    public void getFavorite(Activity activity, String loginToken, NetworkManager.NetworkManagerListener<FavoriteData> listener) {
        Log.e(LOG_TAG, "getFavorite");
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getFavorite(Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, FavoriteData.class, listener);
    }

    public void getPoints(Activity activity, String type, String a_id, String keyword, String user_lat, String user_lng, String range,
                          String loginToken, NetworkManager.NetworkManagerListener<PointData> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().getPoints(type, a_id, keyword, user_lat, user_lng, range,
                Tools.getInstance().getDeviceId(activity), loginToken, currentTimestamp + "", mac);
        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, PointData.class, listener);
    }

    public void getGeoFenceData(Activity activity,
                                NetworkManager.NetworkManagerListener<GeoFenceResponse[]> listener) {
        getGeoFenceData(activity, 0, 0, "", listener);
    }

    public void getGeoFenceData(Activity activity, double userLat, double userLng, String loginToken,
                                NetworkManager.NetworkManagerListener<GeoFenceResponse[]> listener) {

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().getGeoFence("", userLat, userLng, "1",
                NetworkManager.getInstance().getDeviceId(activity),
                loginToken,
                currentTimestamp + "",
                mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, false, call, GeoFenceResponse[].class, listener);
    }

    public void sdkLogin(Activity activity, String parameter, NetworkManager.NetworkManagerListener<CheckUserLoginData> listener) {

        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonResponse> call = getY5CityService().sdkLogin(parameter,
                NetworkManager.getInstance().getDeviceId(activity),
                currentTimestamp + "",
                mac);

        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, CheckUserLoginData.class, listener);
    }

    public void checkLogin(Activity activity, String loginToken, String firebaseTokenId,
                           NetworkManager.NetworkManagerListener<CheckUserLoginData[]> listener) {

        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getY5CityService().checkLogin(loginToken,
                NetworkManager.getInstance().getDeviceId(activity),
                firebaseTokenId,
                currentTimestamp + "",
                mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, CheckUserLoginData[].class, listener);
    }

    public void userTripCover(Activity activity, String loginToken, String t_id, String img_path, String c_id, NetworkManager.NetworkManagerListener<UserTripData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        String mediaType = "image/jpg";
        if (img_path.contains(".png")) {
            mediaType = "image/png";
        }

        File file = new File(img_path);
        RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("trip_cover", file.getName(), requestFile);

        RequestBody deviceId_rb = RequestBody.create(MediaType.parse("text/plain"), Tools.getInstance().getDeviceId(activity));
        RequestBody loginToken_rb = RequestBody.create(MediaType.parse("text/plain"), loginToken);
        RequestBody t_id_rb = RequestBody.create(MediaType.parse("text/plain"), t_id);
        RequestBody currentTimestamp_rb = RequestBody.create(MediaType.parse("text/plain"), currentTimestamp + "");
        RequestBody mac_rb = RequestBody.create(MediaType.parse("text/plain"), mac);

        Call<CommonArrayResponse> call;
        if (c_id.length() == 0) {
            call = getY5CityService().userTripCoverCustom(deviceId_rb, loginToken_rb,
                    t_id_rb, body, currentTimestamp_rb, mac_rb);

        } else {
            call = getY5CityService().userTripCover(Tools.getInstance().getDeviceId(activity), loginToken,
                    t_id, c_id, currentTimestamp + "", mac);
        }

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, UserTripData[].class, listener);
    }

    public void getProject(Activity activity, NetworkManager.NetworkManagerListener<ProjectData[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = NetworkManager.getInstance()
                .getRetrofitForProject().create(Y5CityService.class)
                .getProject(currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, ProjectData[].class, listener);
    }
}
