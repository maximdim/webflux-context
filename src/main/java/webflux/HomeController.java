package webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    public static final String HOME_PATH = "/home";

    @GetMapping(HOME_PATH)
    public String home() {
        return "ok";
    }

}
