package ar.com.marete.apivscodev2.service;

import ar.com.marete.apivscodev2.controller.HelloResponse;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public HelloResponse getHello() {
        return new HelloResponse("Hello from APIVSCODEV2!");
    }
}
