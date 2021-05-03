package kr.co.spotbuddy.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping("/docs")
    public String getApiDocs() {
        return "api-docs";
    }

    @GetMapping("/docs-new")
    public String getNewApiDocs() {
        return "doc/api-docs";
    }
}
