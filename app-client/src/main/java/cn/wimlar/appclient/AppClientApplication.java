package cn.wimlar.appclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableCircuitBreaker
@EnableZuulProxy
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AppClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppClientApplication.class, args);
    }
}


@FeignClient("app-service")
interface UserReader {
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    Resources<User> read();
}

class User {
    String name;

    public String getName() {
        return name;
    }
}

@RestController
@RequestMapping("/users")
class UserApiGatewayController {
    private final UserReader userReader;

    @Autowired
    public UserApiGatewayController(UserReader userReader) {
        this.userReader = userReader;
    }

    @HystrixCommand(fallbackMethod = "getUserNamesFallback")
    @GetMapping("/names")
    public Collection<String> getUserNames() {
        return userReader.read()
                .getContent()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }

    public Collection<String> getUserNamesFallback() {
        return new ArrayList<>();
    }
}