package jp.co.sss.shop.controller.admin.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告削除系
 * 
 */
@Controller
@RequestMapping("/admin/ad/delete")
public class AdminAdDeleteController {

	/**
	 * 広告情報を管理するリポジトリ
	 */
    @Autowired
    PromotionsRepository promotionsRepository;

   
    /**
     * 削除機能
     * @param id ID
     * @return 一覧へ
     */
    @RequestMapping(path = "/execute", method = RequestMethod.POST)
    @Transactional
    public String execute(Integer id) {
        promotionsRepository.findById(id).ifPresent(promo -> {
            promo.setDeleteFlag(1); // 論理削除
            promotionsRepository.save(promo);
        });
        
        // リダイレクト先に削除完了フラグを付けて一覧へ戻す
        return "redirect:/admin/ad/list?deleted=true";
    }
}