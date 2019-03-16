package miaoshaproject.error;

/**
 * Created by asus on 2019/3/14.
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
