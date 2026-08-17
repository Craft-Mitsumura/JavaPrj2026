package jp.co.sss.shop.controller.admin.prize;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.form.PrizeForm;
import jp.co.sss.shop.repository.PrizeRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.UploadFileService;

@Controller
public class AdminPrizeUpdateController {

	@Autowired
	PrizeRepository prizeRepository;

	@Autowired
	BeanTools beanTools;

	@Autowired
	HttpSession session;

	@Autowired
	UploadFileService upfileService;

	/**
	 * 編集画面初期表示
	 */
	@RequestMapping(path = "/admin/prize/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		Prize prize = prizeRepository.findById(id).orElse(null);

		if (prize == null) {
			return "redirect:/syserror";
		}

		PrizeForm prizeForm = beanTools.copyEntityToPrizeForm(prize);

		session.setAttribute("prizeForm", prizeForm);

		return "redirect:/admin/prize/update/input";
	}

	/**
	 * 編集画面表示
	 */
	@RequestMapping(path = "/admin/prize/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		PrizeForm prizeForm = (PrizeForm) session.getAttribute("prizeForm");

		if (prizeForm == null) {
			return "redirect:/syserror";
		}

		BindingResult result = (BindingResult) session.getAttribute("result");

		if (result != null) {
			model.addAttribute(
					"org.springframework.validation.BindingResult.prizeForm",
					result);
			session.removeAttribute("result");
		}

		// ファイルサイズエラーをセッションから取得
		String errorMessage = (String) session.getAttribute("errorMessage");

		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute("errorMessage");
		}

		model.addAttribute("prizeForm", prizeForm);

		return "admin/prize/update_input";
	}

	@RequestMapping(path = "/admin/prize/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute PrizeForm form,
			BindingResult result, Model model) {

		// セッションに保存されている元データ
		PrizeForm lastPrizeForm = (PrizeForm) session.getAttribute("prizeForm");

		if (lastPrizeForm == null) {
			return "redirect:/syserror";
		}

		// 入力内容をセッションへ保存
		session.setAttribute("prizeForm", form);

		// 入力エラー
		boolean hasError = result.hasErrors();

		// 画像サイズチェック
		long maxSize = 1024 * 1024; // 1MB

		if (form.getImageFile() != null
				&& !form.getImageFile().isEmpty()
				&& form.getImageFile().getSize() > maxSize) {

			session.setAttribute(
					"errorMessage",
					"画像は1MB以内のものを選択してください。");

			hasError = true;
		}

		// エラーがある場合
		if (hasError) {
			session.setAttribute("result", result);
			session.setAttribute("prizeForm", form);

			return "redirect:/admin/prize/update/input";
		}

		// 画像アップロード
		String imageName = upfileService.saveUploadFile(form.getImageFile());

		if (imageName != null) {
			form.setImage(imageName);
		} else {
			// 新しい画像を選択しなかった場合は以前の画像を使用
			form.setImage(lastPrizeForm.getImage());
		}

		return "redirect:/admin/prize/update/check";
	}

	@RequestMapping(path = "/admin/prize/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {

		PrizeForm prizeForm = (PrizeForm) session.getAttribute("prizeForm");

		if (prizeForm == null) {
			return "redirect:/syserror";
		}

		model.addAttribute("prizeForm", prizeForm);

		return "admin/prize/update_check";
	}

	@RequestMapping(path = "/admin/prize/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		PrizeForm prizeForm = (PrizeForm) session.getAttribute("prizeForm");

		if (prizeForm == null) {
			return "redirect:/syserror";
		}

		Prize prize = prizeRepository.findById(prizeForm.getId()).orElse(null);

		if (prize == null) {
			return "redirect:/syserror";
		}

		prize.setName(prizeForm.getName());
		prize.setRequiredPoint(prizeForm.getRequiredPoint());
		prize.setImage(prizeForm.getImage());
		prize.setDescription(prizeForm.getDescription());

		prizeRepository.save(prize);

		session.removeAttribute("prizeForm");

		return "redirect:/admin/prize/update/complete";
	}

	@RequestMapping(path = "/admin/prize/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {

		return "admin/prize/update_complete";
	}
}