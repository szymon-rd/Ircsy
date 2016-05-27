package pl.jaca.ircsy.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Jaca777
 *         Created 2016-05-27 at 00
 */
@Controller
@RequestMapping("/chat")
public class ChatHomeController {
    @RequestMapping(method = GET)
    public String chatHome(){
        return "chat";
    }
}
