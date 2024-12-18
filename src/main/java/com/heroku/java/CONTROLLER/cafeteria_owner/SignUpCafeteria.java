package com.heroku.java.CONTROLLER.cafeteria_owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.cafe.CafeBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SignUpCafeteria {
    private final DataSource dataSource;

    @Autowired
    public SignUpCafeteria(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/cafeRegister")
    public String registerCafe(@ModelAttribute("cafeRegister")CafeBean t){
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO public.cafeteria_owner(\"cafeNumber\", \"cafeName\", \"cafeEmail\",\"phoneNumber\", \"cafePassword\") VALUES(?,?,?,?,?)";
            final var statement = connection.prepareStatement(sql);

            String name= t.getCafeName();
            String email=t.getCafeEmail();
            String number=t.getCafeNumber();
            String phone =t.getCafePhone();
            String password =t.getCafePassword();
    
            statement.setString(1,number);
            statement.setString(2,name);
            statement.setString(3,email);
            statement.setString(4,phone);
            statement.setString(5,password);
            
            statement.executeUpdate();
            
            // System.out.println("type : "+protype);
            // System.out.println("product price : RM"+proprice);
            // System.out.println("proimg: "+proimgs.getBytes());
            
            connection.close();
                
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/cafeRegister";
                }
                
            return "redirect:/cafesignin";
    }
}
