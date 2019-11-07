package com.example.cseproject.Controller;

import com.example.cseproject.Model.User;
import com.example.cseproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class UserController {
    @Autowired
    UserService service;
    @GetMapping(path="/add") // Map ONLY GET Requests
    public @ResponseBody String addNewUserController (@RequestParam String name, @RequestParam String email) {
       return service.addNewUser(name,email);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsersController() {
        return service.getAllUsers();
    }
}
