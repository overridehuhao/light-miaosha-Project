package miaoshaproject.service.Impl;

import miaoshaproject.dao.UserDAOMapper;
import miaoshaproject.dao.UserPasswordDAOMapper;
import miaoshaproject.dataobject.UserDAO;
import miaoshaproject.dataobject.UserPasswordDAO;
import miaoshaproject.error.BusinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.service.UserService;
import miaoshaproject.service.model.UserModel;
import miaoshaproject.validator.ValidationResult;
import miaoshaproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by asus on 2019/3/14.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAOMapper userDAOMapper;

    @Autowired
    private UserPasswordDAOMapper userPasswordDAOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userDaoMapper获取到对应的用户的DataObject
      UserDAO userDAO=userDAOMapper.selectByPrimaryKey(id);

      if(userDAO==null){
          return null;
      }
      //通过用户id获取对应的用户加密密码信息
      UserPasswordDAO userPasswordDAO=userPasswordDAOMapper.selectByUserId(userDAO.getId());

      return convertFromDataObject(userDAO,userPasswordDAO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        ValidationResult result= validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //实现model-》dataobject
        UserDAO userDAO=convertFromModel(userModel);
        userDAOMapper.insertSelective(userDAO);

        userModel.setId(userDAO.getId());

        UserPasswordDAO userPasswordDAO=convertPswFromModel(userModel);
        userPasswordDAOMapper.insertSelective(userPasswordDAO);

        return;
    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BusinessException {
        //通过用户手机获取用户信息
        UserDAO userDAO=userDAOMapper.selectByTelephone(telephone);
        if(userDAO==null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDAO userPasswordDA=userPasswordDAOMapper.selectByUserId(userDAO.getId());
        UserModel userModel=convertFromDataObject(userDAO,userPasswordDA);

        //比对用户信息内加密的密码相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserDAO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }

        UserDAO userDAO=new UserDAO();
        BeanUtils.copyProperties(userModel,userDAO);
        userDAO.setTelphone(userModel.getTelephone());
        return userDAO;
    }

    private UserPasswordDAO convertPswFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserPasswordDAO userPasswordDAO=new UserPasswordDAO();
        userPasswordDAO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDAO.setUserId(userModel.getId());
        return userPasswordDAO;
    }

    private UserModel convertFromDataObject(UserDAO userDAO, UserPasswordDAO userPasswordDAO){
        if(userDAO==null){
            return  null;
        }
        UserModel userModel=new UserModel();
        BeanUtils.copyProperties(userDAO,userModel);

        if(userPasswordDAO!=null) {
            userModel.setEncrptPassword(userPasswordDAO.getEncrptPassword());
        }

        return userModel;
    }
}
