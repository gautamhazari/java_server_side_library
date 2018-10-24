package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@EnableAutoConfiguration
@RequestMapping(path = "server_side_api"/*, produces = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
public class CommonController {
    protected ConcurrentCache sessionCache;

}
