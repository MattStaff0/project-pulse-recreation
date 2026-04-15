package team.projectpulse.system;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all non-API, non-static routes to index.html for Vue Router
        registry.addViewController("/{spring:[\\w-]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{path1:[\\w-]+}/{path2:[\\w-]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{path1:[\\w-]+}/{path2:[\\w-]+}/{path3:[\\w-]+}")
                .setViewName("forward:/index.html");
    }
}
