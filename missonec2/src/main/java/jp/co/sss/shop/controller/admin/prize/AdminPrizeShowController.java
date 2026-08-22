package jp.co.sss.shop.controller.admin.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.repository.PrizeRepository;

@Controller
public class AdminPrizeShowController {

    @Autowired
    PrizeRepository prizeRepository;

    // 景品詳細
    @RequestMapping(path = "/admin/prize/show/{id}", method = RequestMethod.GET)
    public String showPrize(@PathVariable Integer id, Model model) {

        Prize prize = prizeRepository.findById(id).orElse(null);

        if (prize == null) {
            return "redirect:/admin/prize/list";
        }

        model.addAttribute("prize", prize);

        return "admin/prize/show";
    }

}