package miaoshaproject.service.Impl;

import miaoshaproject.dao.PromoDAOMapper;
import miaoshaproject.dataobject.PromoDAO;
import miaoshaproject.service.PromoService;
import miaoshaproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by asus on 2019/3/16.
 */
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDAOMapper promoDAOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDAO promoDAO=promoDAOMapper.selectByItemId(itemId);

        //dataObject->Model
        PromoModel promoModel=convertFromDataObject(promoDAO);

        //判断当前时间秒杀活动是否即将开始或正在进行
        DateTime now=new DateTime();
        if(promoModel==null){
            return  null;
        }
        if(promoModel.getStartTime().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndTime().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDAO promoDAO){
        if(promoDAO==null){
            return null;
        }
        PromoModel promoModel=new PromoModel();
        BeanUtils.copyProperties(promoDAO,promoModel);
        promoModel.setStartTime(new DateTime(promoDAO.getStartTime()));
        promoModel.setEndTime(new DateTime(promoDAO.getEndTime()));
        promoModel.setPromoItemPrice(new BigDecimal(promoDAO.getPromoItemPrice()));

        return promoModel;
    }
}
