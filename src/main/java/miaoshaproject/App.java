package miaoshaproject;

import miaoshaproject.dao.UserDAOMapper;
import miaoshaproject.dataobject.UserDAO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"miaoshaproject"})
@RestController
@MapperScan("miaoshaproject.dao")
public class App 
{
    @Autowired
    private UserDAOMapper userDAOMapper;

    @RequestMapping("/")
    public String say(@RequestParam(name="id")Integer id){
        UserDAO userDAO=userDAOMapper.selectByPrimaryKey(id.intValue());
        if(userDAO==null){
            return "用户不存在";
        }
        else
        return userDAO.getName();
    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class,args);
    }
}
