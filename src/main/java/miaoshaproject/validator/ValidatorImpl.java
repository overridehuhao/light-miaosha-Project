package miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

/**
 * Created by asus on 2019/3/15.
 */
@Component
public class ValidatorImpl implements InitializingBean{

    private javax.validation.Validator validator;

    //实现检验方法并返回校验结果
    public ValidationResult validate(Object bean){
        final ValidationResult validationResult=new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet= validator.validate(bean);
        if(constraintViolationSet.size()>0){
            validationResult.setHasErrors(true);
            constraintViolationSet.forEach(constraintViolation->{
                String errMsg=constraintViolation.getMessage();
                String propertyName=constraintViolation.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return  validationResult;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂的初始化方式使其实例化
        this.validator= Validation.buildDefaultValidatorFactory().getValidator();

    }
}
