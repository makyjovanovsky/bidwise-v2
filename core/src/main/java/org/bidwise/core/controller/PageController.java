package org.bidwise.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/")
    public String getPage() {
        return "hello";
    }

    @GetMapping("/hello")
    public String getHelloPage() {
        return "hello";
    }
}
