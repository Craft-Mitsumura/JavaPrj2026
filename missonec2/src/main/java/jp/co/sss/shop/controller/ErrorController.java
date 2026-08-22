package jp.co.sss.shop.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * エラー画面表示機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ErrorController {

	/**
	 * エラー画面表示処理
	 *
	 * @param session セッション情報
	 * @return "error" エラー画面へ
	 */
	@RequestMapping(path = "/syserror")
	public String error(HttpSession session) {
		// セッション情報を無効にする
		session.invalidate();
		return "error";
	}
}
