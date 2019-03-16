package miaoshaproject.service.Impl;

import miaoshaproject.dao.ItemDAOMapper;
import miaoshaproject.dao.ItemStockDAOMapper;
import miaoshaproject.dataobject.ItemDAO;
import miaoshaproject.dataobject.ItemStockDAO;
import miaoshaproject.error.BusinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.service.ItemService;
import miaoshaproject.service.PromoService;
import miaoshaproject.service.model.ItemModel;
import miaoshaproject.service.model.PromoModel;
import miaoshaproject.validator.ValidationResult;
import miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by asus on 2019/3/15.
 */
@Service
public class ItemServiceImpl implements ItemService{

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDAOMapper itemDAOMapper;

    @Autowired
    private ItemStockDAOMapper itemStockDAOMapper;

    @Autowired
    private PromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result=validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //转化ItemModel变为Object
        ItemDAO itemDAO=convertItemDAOFromItemModel(itemModel);

        //写入数据库
        itemDAOMapper.insertSelective(itemDAO);
        itemModel.setId(itemDAO.getId());

        ItemStockDAO itemStockDAO=convertItemStockDAOFromItemModel(itemModel);
        itemStockDAOMapper.insertSelective(itemStockDAO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    private ItemDAO convertItemDAOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemDAO itemDAO=new ItemDAO();
        BeanUtils.copyProperties(itemModel,itemDAO);
        itemDAO.setPrice(itemModel.getPrice().doubleValue());
        return itemDAO;
    }

    private ItemStockDAO convertItemStockDAOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemStockDAO itemStockDAO=new ItemStockDAO();
        itemStockDAO.setItemId(itemModel.getId());
        itemStockDAO.setStock(itemModel.getStock());
        return itemStockDAO;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDAO> itemDAOList=itemDAOMapper.listItem();
        List<ItemModel> itemModelList=itemDAOList.stream().map(itemDAO -> {
            ItemStockDAO itemStockDAO=itemStockDAOMapper.selectByItemId(itemDAO.getId());
            ItemModel itemModel=convertModelFromObject(itemDAO,itemStockDAO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDAO itemDAO=itemDAOMapper.selectByPrimaryKey(id);
        if(itemDAO==null){
            return null;
        }
        //操作获得库存数量
        ItemStockDAO itemStockDAO=itemStockDAOMapper.selectByItemId(itemDAO.getId());

        //将dataobject-》model
        ItemModel itemModel=convertModelFromObject(itemDAO,itemStockDAO);

        //获取活动商品信息
        PromoModel promoModel= promoService.getPromoByItemId(itemModel.getId());
        if(promoModel!=null&& promoModel.getStatus().intValue()!=3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    private ItemModel convertModelFromObject(ItemDAO itemDAO,ItemStockDAO itemStockDAO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDAO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDAO.getPrice()));
        itemModel.setStock(itemStockDAO.getStock());

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException{
        int affectRows=itemStockDAOMapper.decreaseStock(itemId,amount);
        if(affectRows>0){
            //更新库存成功
            return true;
        }else{
            //更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDAOMapper.increaseSales(itemId,amount);
    }
}
