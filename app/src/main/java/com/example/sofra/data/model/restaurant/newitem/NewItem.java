
package com.example.sofra.data.model.restaurant.newitem;

import com.example.sofra.data.model.general.itemrestaurant.ItemRestaurantData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewItem {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private NewItemData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public NewItemData getData() {
        return data;
    }

    public void setData(NewItemData data) {
        this.data = data;
    }

}
