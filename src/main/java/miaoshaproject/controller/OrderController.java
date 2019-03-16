package miaoshaproject.controller;

import miaoshaproject.error.BusinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.OrderService;
import miaoshaproject.service.model.OrderModel;
import miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by asus on 2019/3/16.
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials ="true",allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/createorder")
    @ResponseBody
    //封装下单请求
    public CommonReturnType createOrder(@RequestParam(name="itemId")Integer itemId,
                                        @RequestParam(name="amount")Integer amount,
                                        @RequestParam(name="promoId",required = false)Integer promoId) throws BusinessException {
        //获取用户的登录信息
        Boolean isLogin=(Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null || !isLogin.booleanValue()){
            throw new BusinessException(EmBusinessError.USER_NOY_LOGIN,"用户还未登录，不能下单");
        }
        UserModel userModel=(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel=orderService.createOrder(userModel.getId(),itemId,promoId,amount);
        return CommonReturnType.create(null);
    }
}
