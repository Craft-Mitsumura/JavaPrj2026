package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.service.MailService;

/**
 * お問い合わせ・レビュー機能のコントローラクラス
 *
 * @author sagar / 手塚
 */
@Controller
public class ClientReviewController {

	private final MailService mailService;

	ClientReviewController(MailService mailService) {
		this.mailService = mailService;
	}

	/**
	 * お問い合わせフォーム表示
	 * @author sagar
	 * @return client/review/reviewForm お問い合わせフォーム画面
	 */
	@GetMapping("/client/review/form")
	public String showReviewForm() {

		return "client/review/reviewForm";
	}

	/**
	 * お問い合わせフォーム送信処理
	 * @param name お客様の名前
	 * @param email お客様のメールアドレス
	 * @param subject お問い合わせの件名
	 * @param message お問い合わせの内容
	 * @return リダイレクト先のURL
	 */
	@PostMapping("/client/review/input")
	public String review(
			@RequestParam String name,
			@RequestParam String email,
			@RequestParam String subject,
			@RequestParam String message) {

		mailService.sendMail(name, email, subject, message);

		return "redirect:/";
	}

}