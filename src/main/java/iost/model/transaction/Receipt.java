package iost.model.transaction;

import com.google.gson.annotations.SerializedName;

public class Receipt {
    @SerializedName("func_name")
    public String funcName;
    public String content;
}
