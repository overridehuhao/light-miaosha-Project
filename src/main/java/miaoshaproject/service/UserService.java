package miaoshaproject.service;

import miaoshaproject.error.BusinessException;
import miaoshaproject.service.model.UserModel;

/**
 * Created by asus on 2019/3/14.
 */
public interface UserService {
    //通过用户id获取用户对象的方法
   UserModel getUserById(Integer id);

   void register(UserModel userModel) throws BusinessException;

   UserModel validateLogin(String telephone,String encrptPassword) throws BusinessException;
}
