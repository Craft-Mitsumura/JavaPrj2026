package jp.co.sss.shop.config;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 動作環境設定クラス
 *
 * @author SystemShared
 */
@Configuration
public class SharedShopConfig implements WebMvcConfigurer {

	/**
	 * リソースハンドラの追加 静的ファイル(js,css,image)へのパスを指定することにより、SpringBoot経由でアクセス可能にする
	 *
	 * @param registry リソースハンドラのレジストリ
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		//画像ファイルの保存場所と、パス名をつなげる設定
		registry.addResourceHandler("/images/**").addResourceLocations("file:images/");
	}

	/**
	 * ページング処理の設定を追加
	 *
	 * @param argumentResolvers リゾルバ
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		// ページ単位に表示する件数
		resolver.setFallbackPageable(PageRequest.of(0, 10));
		argumentResolvers.add(resolver);
	}

	/**
	 * セッションIDの扱いについての設定
	 *
	 * @return サーブレットコンテキストの初期化
	 */
	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
				SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
				sessionCookieConfig.setHttpOnly(true);
			}
		};
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable(); // デフォルトサーブレットへの転送機能を有効化
	}
}
