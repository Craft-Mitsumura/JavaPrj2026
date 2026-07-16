package jp.co.sss.shop.controller.admin.ad;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.sss.shop.repository.PromotionsRepository;
import jp.co.sss.shop.service.PromotionConverter;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告表示系
 *  
 */

@Controller
public class AdminAdShowController{
	
	/**
	 * 広告情報
	 */
	@Autowired
	PromotionsRepository repository;
	
	/**
	 * JSONコンバーター
	 */
	@Autowired
	PromotionConverter converter;
	
	/**
	 * セッション情報 
	 */
	@Autowired
	HttpSession session; 
	
    /**
     * 広告一覧表示
     * URL: /admin/ad/list
     * @param model モデル
	 * @return 広告一覧
     */
    @GetMapping("/admin/ad/list")
    public String list(Model model) {
        // 削除フラグを引数に検索
    	model.addAttribute("promotions", repository.findByDeleteFlagOrderByIsActiveDescIdAsc(0));
        
        return "admin/ad/list";
	}
    
    
}
