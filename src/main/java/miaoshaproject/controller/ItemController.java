package miaoshaproject.controller;

import miaoshaproject.controller.viewObject.ItemVO;
import miaoshaproject.error.BusinessException;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.ItemService;
import miaoshaproject.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by asus on 2019/3/15.
 */
@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials ="true",allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    //创建商品的controller
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name="title")String title, @RequestParam(name="description")String description,
                                       @RequestParam(name="price")BigDecimal price, @RequestParam(name="stock")Integer stock,
                                       @RequestParam(name="imgUrl")String imgUrl) throws BusinessException {
        //封装service请求来构建商品
        ItemModel itemModel=new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn=itemService.createItem(itemModel);
        ItemVO itemVO=convertItemVOFromItemModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    //商品详情页浏览
    @RequestMapping(value = "/get",method = {RequestMethod.GET})//,consumes = {CONTENT_TYPE_FORMED}
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id")Integer id){
        ItemModel itemModel=itemService.getItemById(id);

        ItemVO itemVO=convertItemVOFromItemModel(itemModel);

        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return  null;
        }
        ItemVO itemVO=new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel()!=null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartTime(itemModel.getPromoModel().getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());

        }else{
            itemVO.setPromoStatus(0);
        }

        return itemVO;
    }

    //商品列表浏览
    @RequestMapping(value = "/list",method = {RequestMethod.GET})//,consumes = {CONTENT_TYPE_FORMED}
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList=itemService.listItem();
        //使用stream api将list内的ItemModel转化为ItemVO
        List<ItemVO> itemVOLIst=itemModelList.stream().map(itemModel -> {
            ItemVO itemVO=this.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOLIst);
    }
}
