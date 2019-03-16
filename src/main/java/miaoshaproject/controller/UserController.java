package miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import miaoshaproject.controller.viewObject.UserVO;
import miaoshaproject.error.BusinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.UserService;
import miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by asus on 2019/3/14.
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials ="true",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telephone")String telephone,
                                  @RequestParam(name="password")String password) throws BusinessException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，校验用户登录是否合法
        UserModel userModel=userService.validateLogin(telephone,this.EncodeByMD5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }

    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telephone")String telephone,
    @RequestParam(name="otpCode")String otpCode,@RequestParam(name="name")String name
    ,@RequestParam(name="gender")Integer gender,@RequestParam(name="age")Integer age
            ,@RequestParam(name="password")String password)throws BusinessException{
       //验证手机号与相应的otpcode
       String  inSessionOtpCode=(String)this.httpServletRequest.getSession().getAttribute(telephone);
       if(!StringUtils.equals(otpCode,inSessionOtpCode)){
           throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMD5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncodeByMD5(String str){
        //确定计算方法
        MessageDigest md5=null;
        try {
        md5=MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        BASE64Encoder base64Encoder=new BASE64Encoder();
        //加密字符串
        byte[] ans=null;
        try{
           ans= md5.digest(str.getBytes("utf-8"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String newstr=base64Encoder.encode(ans);
        return newstr;
    }

    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    //用户获取otp短信接口
    public CommonReturnType getOtp(@RequestParam(name="telephone")String telephone){
        //按照一定规则生成otp验证码
        Random random=new Random();
        int randomInt=random.nextInt(99999);
        randomInt+=10000;
        String otpCode=String.valueOf(randomInt);

        //将otp验证码同用户的手机号关联，使用httpseccession的方式绑定手机号与otpcode
        httpServletRequest.getSession().setAttribute(telephone,otpCode);

        //将otp验证码通过短信通道发送给用户，省略
        System.out.println("telphone="+telephone+"&otpCode="+otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id")Integer id)throws BusinessException{
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel=userService.getUserById(id);

        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO=convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    //将核心领域模型转化为可供UI使用的viewObject
    private UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }

        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);

        return userVO;
    }
}
