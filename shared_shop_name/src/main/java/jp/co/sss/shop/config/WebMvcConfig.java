package jp.co.sss.shop.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.co.sss.shop.config.interceptor.CarouselInterceptor;

/**
 * 
 * 金城（チームF）
 * uploadsの静的リソースとしての追加
 *
 * @author SystemShared
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/images/uploads/");
    }
    
    /** カルーセルインターセプター */
    @Autowired
    private CarouselInterceptor carouselInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ルートパス（トップ画面）だけに広告データを仕込む
        registry.addInterceptor(carouselInterceptor).addPathPatterns("/");
    }
}