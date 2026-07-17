package jp.co.sss.shop.controller.admin.prize;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.form.PrizeForm;
import jp.co.sss.shop.repository.PrizeRepository;
import jp.co.sss.shop.service.UploadFileService;
import jp.co.sss.shop.util.Constant;


@Controller
public class AdminPrizeRegistController {


    @Autowired
    HttpSession session;


    @Autowired
    PrizeRepository prizeRepository;

    @Autowired
    UploadFileService upfileService;


    // 登録ボタン押下
    @RequestMapping(path="/admin/prize/regist/input", method=RequestMethod.POST)
    public String registInput() {

        PrizeForm prizeForm = (PrizeForm) session.getAttribute("prizeForm");

        if(prizeForm == null) {
            session.setAttribute("prizeForm", new PrizeForm());
        }

        return "redirect:/admin/prize/regist/input";
    }



    // 入力画面
    @RequestMapping(path="/admin/prize/regist/input", method=RequestMethod.GET)
    public String registInput(Model model) {

        PrizeForm prizeForm =
                (PrizeForm) session.getAttribute("prizeForm");


        if(prizeForm == null) {
            return "redirect:/syserror";
        }
        
        BindingResult result =
                (BindingResult) session.getAttribute("result");

        if(result != null) {

            model.addAttribute(
                "org.springframework.validation.BindingResult.prizeForm",
                result
            );

            session.removeAttribute("result");
        }


        model.addAttribute("prizeForm", prizeForm);


        return "admin/prize/regist_input";
    }



    // 入力確認
    @RequestMapping(path="/admin/prize/regist/check", method=RequestMethod.POST)
    public String registInputCheck(
            @Valid @ModelAttribute PrizeForm form,
            BindingResult result) {


        session.setAttribute("prizeForm", form);


        if(result.hasErrors()) {

            session.setAttribute("result", result);

            return "redirect:/admin/prize/regist/input";
        }
        
     // 画像アップロード
        String imageName =
                upfileService.saveUploadFile(form.getImageFile());

        if(imageName != null) {
            form.setImage(imageName);
        }

        // 画像名を反映したFormを再保存
        session.setAttribute("prizeForm", form);


        return "redirect:/admin/prize/regist/check";
    }



    // 確認画面
    @RequestMapping(path="/admin/prize/regist/check", method=RequestMethod.GET)
    public String registCheck(Model model) {


        PrizeForm prizeForm =
                (PrizeForm) session.getAttribute("prizeForm");


        if(prizeForm == null) {
            return "redirect:/syserror";
        }


        model.addAttribute("prizeForm", prizeForm);


        return "admin/prize/regist_check";
    }



    // 登録処理
    @RequestMapping(path="/admin/prize/regist/complete", method=RequestMethod.POST)
    public String registComplete() {


        PrizeForm prizeForm =
                (PrizeForm) session.getAttribute("prizeForm");


        if(prizeForm == null) {
            return "redirect:/syserror";
        }



        Prize prize = new Prize();

        prize.setName(prizeForm.getName());
        prize.setRequiredPoint(prizeForm.getRequiredPoint());
        prize.setImage(prizeForm.getImage());
        prize.setDescription(prizeForm.getDescription());

        // 未削除
        prize.setDeleteFlag(Constant.NOT_DELETED);


        prizeRepository.save(prize);


        session.removeAttribute("prizeForm");


        return "redirect:/admin/prize/regist/complete";
    }



    // 完了画面
    @RequestMapping(path="/admin/prize/regist/complete", method=RequestMethod.GET)
    public String registCompleteFinish() {

        return "admin/prize/regist_complete";
    }

}