package jp.co.sss.shop.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.co.sss.shop.util.URLCheck;

/**
 * ログインチェック用フィルタ
 * 　未ログイン状態でのチェック
 * 
 * @author System Shared
 */

public class LoginCheckFilter extends HttpFilter {
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// リクエストURLを取得
		String requestURL = request.getRequestURI();

		if (URLCheck.isURLForNonLogin(requestURL, request.getContextPath())) {

			// セッション情報を取得
			HttpSession session = request.getSession();

			if (session.getAttribute("user") == null) {
				// 不正アクセスの場合、ログイン画面にリダイレクト

				// ログイン画面へリダイレクト
				response.sendRedirect(request.getContextPath() + "/login");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

}
