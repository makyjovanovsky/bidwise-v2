package org.bidwise.producer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/")
    public String getPage() {
        return "hello";
    }
}
