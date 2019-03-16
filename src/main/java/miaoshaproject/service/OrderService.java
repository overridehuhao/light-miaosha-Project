package miaoshaproject.service;

import miaoshaproject.error.BusinessException;
import miaoshaproject.service.model.OrderModel;

/**
 * Created by asus on 2019/3/15.
 */
public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;
}
