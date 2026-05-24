package ar.com.marete.apivscodev2.service;

import ar.com.marete.apivscodev2.controller.HelloResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    void getHello_returnsExpectedMessage() {
        HelloResponse response = helloService.getHello();
        assertThat(response.message()).isEqualTo("Hello from APIVSCODEV2!");
    }

    @Test
    void getHello_responseIsNotNull() {
        assertThat(helloService.getHello()).isNotNull();
    }
}
