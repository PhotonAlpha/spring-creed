package creed.resource.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExampleController {
    @GetMapping("/echo")
    public String demo() {
      return "示例返回";
    }

    @GetMapping("/home")
    public String home() {
      return "我是首页";
    }

    @GetMapping("/admin")
    public String admin() {
      return "我是管理员";
    }

    @GetMapping("/normal")
    public String normal() {
      return "我是普通用户";
    }
}
