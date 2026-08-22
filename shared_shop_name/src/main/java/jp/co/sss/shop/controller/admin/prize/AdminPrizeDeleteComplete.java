package jp.co.sss.shop.controller.admin.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.repository.PrizeRepository;

@Controller
public class AdminPrizeDeleteComplete {

    @Autowired
    PrizeRepository pRepo;

    @PostMapping("/admin/prize/delete/complete")
    public String deleteComplete(
            @RequestParam("id") Integer id) {

        pRepo.deleteById(id);

        System.out.println("景品を削除しました");

        return "admin/prize/prizeDeleteComplete";
    }
}