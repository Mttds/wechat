package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.net.InetAddress;

@SpringBootApplication
@RestController
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /*
     * Login Page
     * 
     * @GetMapping can be also implemented with:
     * 
     * @RequestMapping(value = "/", method = RequestMethod.GET)
     */
    @GetMapping("/")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /*
     * Chatroom Page
     */
    @GetMapping("/index")
    public ModelAndView index(String username, HttpServletRequest request) throws UnknownHostException {
        ModelAndView mdv = new ModelAndView("chat"); // using chat thymeleaf template engine
        if (username == null || username.isEmpty()) {
            username = "Anonymous";
        }

        mdv.addObject("username", username);
        mdv.addObject("ipaddress", InetAddress.getLocalHost().getHostAddress());
        mdv.addObject(
            "webSocketUrl",
            "ws://" + InetAddress.getLocalHost().getHostAddress() + ":" + request.getServerPort()
            + request.getContextPath() + "/index/" + username
        );

        System.out.println(
            "[INFO] - Websocket URL: " + "ws://" + "localhost:" + request.getServerPort()
            + request.getContextPath() + "/index?username=" + username + " | Ip Address: "
            + InetAddress.getLocalHost().getHostAddress()
        );

        return mdv;
    }
}
