package jp.co.sss.shop.controller.admin.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.repository.PrizeRepository;

@Controller

public class AdminPrizeDeleteCheack {
	@Autowired
	PrizeRepository  pRepo ;
	@RequestMapping(path ="/admin/prize/delete/check" , method= RequestMethod.POST)
	public String confirmDelete(
			Model model ,
			@RequestParam("id") Integer id
			
			
			) {
		System.out.println(id);
		model.addAttribute("prize" , pRepo.findById(id).orElse(null));
		System.out.println("削除確認画面に遷移");
		return"admin/prize/prizeDeleteCheck"; 	
	}
}
