package jp.co.sss.shop.controller.admin.ad;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class AdminAdDeleteConfirm {
	@RequestMapping(path ="/admin/ad/delete/confirm" , method= RequestMethod.POST)
	public String confirmDelete() {
		System.out.println("削除確認画面に遷移");
		return"admin/ad/adDeleteConfirm"; 	
	}
}
