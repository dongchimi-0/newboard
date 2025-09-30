import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL → 실제 프로젝트 루트 uploads/ 폴더
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
