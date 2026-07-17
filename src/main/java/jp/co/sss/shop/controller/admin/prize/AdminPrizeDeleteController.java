package jp.co.sss.shop.controller.admin.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.repository.PrizeRepository;

@Controller
public class AdminPrizeDeleteController {

    @Autowired
    PrizeRepository prizeRepository;

    @RequestMapping(path = "/admin/prize/delete", method = RequestMethod.POST)
    public String delete(Integer id) {

        prizeRepository.deleteById(id);

        return "redirect:/admin/prize/list";
    }
}