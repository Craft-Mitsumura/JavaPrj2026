package jp.co.sss.shop.config.interceptor;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * カルーセル広告表示
 * @author 山口、金城（チームF）
 */
@Component
public class CarouselInterceptor implements HandlerInterceptor {

	/** 広告情報を管理するリポジトリ */
    @Autowired
    private PromotionsRepository promotionsRepository;

    /*
     * カルーセル広告の一覧表示
     * @author 山口
     * @param request      HTTPリクエスト
	 * @param response     HTTPレスポンス
	 * @param handler      実行されたハンドラオブジェクト
	 * @param modelAndView コントローラが返したModelAndViewオブジェクト
	 * @throws Exception 広告情報の取得処理で例外が発生した場合
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler, ModelAndView modelAndView) throws Exception {
        
        // トップページ（index）を表示する場合にのみ処理を実行
        if (modelAndView != null && "index".equals(modelAndView.getViewName())) {
            List<Promotions> adList = promotionsRepository.findAllByOrderByIsActiveDescIdDesc();
            modelAndView.addObject("adList", adList);
        }
    }
}