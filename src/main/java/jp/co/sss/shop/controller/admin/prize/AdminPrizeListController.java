package jp.co.sss.shop.controller.admin.prize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.repository.PrizeRepository;

@Controller
public class AdminPrizeListController {

    @Autowired
    PrizeRepository prizeRepository;

    @RequestMapping(path = "/admin/prize/list", method = { RequestMethod.GET, RequestMethod.POST })
    public String showPrizeList(Model model) {
    	
        List<Prize> prizeList = prizeRepository.findAllByOrderByRequiredPointAsc();

        model.addAttribute("prizes", prizeList);

        return "admin/prize/list";
    }
}