package com.smoothsys.qonsume_pos.Retrofit;

import com.smoothsys.qonsume_pos.Models.Credit;
import com.smoothsys.qonsume_pos.Models.ItemClasses.ItemResponseObject;
import com.smoothsys.qonsume_pos.Models.MerchantResponseObject;
import com.smoothsys.qonsume_pos.Models.Message.MessageSentResponse;
import com.smoothsys.qonsume_pos.Models.OrderClasses.OrderResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.RestaurantResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SingleRestaurantResponseObject;
import com.smoothsys.qonsume_pos.Models.SimpleResponse;
import com.smoothsys.qonsume_pos.Models.SubmissionStatusObject;

import org.json.JSONArray;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ISnabbOrderAPI {

    @Headers("Connection:close")
    @POST("merchants/login/")
    @FormUrlEncoded
    Call<MerchantResponseObject> merchantLogin(@Field("email") String merchantEmail, @Field("password") String password);

    @Headers("Connection:close")
    @GET("transactions/shop_transactions/shop_id/{shop_id}")
    Call<OrderResponseObject> getOrders_old(@Path("shop_id") int shopId);

    @Headers("Connection:close")
    @GET("transactions/shop_transactions_date/shop_id/{shop_id}/start_date/{start_date}/end_date/{end_date}")
    Call<OrderResponseObject> getOrders(@Path("shop_id") int shopId, @Path("start_date") String start_date,@Path("end_date") String end_date);

    @Headers("Connection:close")
    @GET("transactions_email/paid_orders/shop_email/{shop_email}")
    Call<OrderResponseObject> merchantStatement(@Path("shop_email") String shopEmail);

    @Headers("Connection:close")
    @GET("transactions_email/user_transactions_email/get/")
    Call<OrderResponseObject> getCustomerStatement(@Query("shop_email") String shopEmail, @Query("customer_email") String customerEmail);

    @Headers("Connection:close")
    @GET("snabbcredits/snabb_credit/")
    Call<List<Credit>> getCustomerCredit(@Query("customer_email") String customerEmail);

    @Headers("Connection:close")
    @GET("payments/daily_transaction_count/")
    Call<String> getTransactionCount(@Query("shop_id") int shopId);

    @Headers("Connection:close")
    @GET("payments/daily_transaction_amount/")
    Call<String> getTransactionAmount(@Query("shop_id") int shopId);

    @Headers("Connection:close")
    @GET("shops/get")
    Call<SingleRestaurantResponseObject> getSingleRestaurant(@Query("id") int shopId);

    @FormUrlEncoded
    @POST("transactions_email/add/")
    Call<SubmissionStatusObject> submitOrder(@Field("orders") JSONArray order);

    @GET("payments/payment/")
    Call<ResponseBody> payOrder(@Query("order_id") int orderId, @Query("shop_id") int shopId, @Query("pay_type") String pay_type, @Query("amount") double amount);

    @GET("snabbcredits/payment/")
    Call<ResponseBody> decrementCredits(@Query("customer_email") String customerEmail,@Query("amount") double amount);

    @GET("payments/payment_status/")
    Call<ResponseBody> paymentStatus(@Query("order_id") int orderId);

    @Headers("Connection:close")
    @GET("items_email/get/")
    Call<ItemResponseObject> getItems_old(@Query("shop_email") String shopEmail, @Query("sub_cat_id") int subCatId);

    @Headers("Connection:close")
    @GET("items/get/shop_id/{shop_id}/sub_cat_id/{sub_cat_id}/item/all")
    Call<ItemResponseObject> getItems(@Path("shop_id") int shopId, @Path("sub_cat_id") int subCatId);

    @FormUrlEncoded
    @POST("transactions/update_status/")
    Call<SubmissionStatusObject> changeOrderStatus(@Field("order_id") int orderId, @Field("order_status") int transactionStatus);

    @FormUrlEncoded
    @POST("transactions/update_status_detail/")
    Call<SimpleResponse> changeDetailStatus(@Field("detail_id") int detailId, @Field("item_state") int itemState);

    @FormUrlEncoded
    @POST("messages/set/")
    Call<MessageSentResponse> sendOrderMessage(@Field("transaction_id") int orderID, @Field("merchant_id") int merchantID, @Field("message") String message);
}