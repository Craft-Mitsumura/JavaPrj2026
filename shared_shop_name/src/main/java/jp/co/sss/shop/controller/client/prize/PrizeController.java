package jp.co.sss.shop.controller.client.prize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.repository.PrizeRepository;

@Controller
@RequestMapping("/client/prize")
public class PrizeController {

    @Autowired
    private PrizeRepository prizeRepository;

    @GetMapping("/list")
    public String list(Model model) {

        List<Prize> prizeList = prizeRepository.findAllByOrderByRequiredPointAsc();

        model.addAttribute("prizeList", prizeList);

        return "client/prize/list";
    }
}