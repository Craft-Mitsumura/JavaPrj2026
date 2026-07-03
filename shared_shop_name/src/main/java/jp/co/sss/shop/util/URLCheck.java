package jp.co.sss.shop.util;

/**
 * リクエストURLチェック　クラス
 * @author System Shared
 *
 */
public class URLCheck {
	/**
	 * 静的ファイルのリクエストURLであるかを判定[
	 * 
	 * @param requestURL リクエストURL
	 * @return true：静的ファイルへのリクエストURLである、false：静的ファイルへのリクエストURLではない
	 */
	public static boolean isURLForStaticFile(String requestURL) {
		boolean isCheckURLOK = false;
		if (requestURL.indexOf(Constant.CSS_FOLDER) != -1
				|| requestURL.indexOf(Constant.IMAGE_FOLDER) != -1) {
			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;

	}

	/**
	 * システム管理者 リクエストURLがアクセス可能かを判定
	 * 
	 * @param requestURL リクエストURL
	 * @return true：アクセス可能、false：アクセス不可
	 */
	public static boolean isURLForSystemAdmin(String requestURL) {
		boolean isCheckURLOK = false;
		if (isURLForStaticFile(requestURL)
				|| requestURL.endsWith("/login")
				|| requestURL.indexOf("admin/menu") != -1
				|| requestURL.indexOf("/admin/admin_menu") != -1
				|| requestURL.indexOf("admin/user") != -1
				|| requestURL.endsWith("/logout")) {
			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;
	}

	/**
	 * 運用管理者 リクエストURLがチェック対象であるかを判定
	 * 
	 * @param requestURL リクエストURL
	 * @return true：チェック対象、false：チェック対象外
	 */
	public static boolean istURLForAdmin(String requestURL) {
		boolean isCheckURLOK = false;
		if (isURLForStaticFile(requestURL)
				|| isURLForSystemAdmin(requestURL)
				|| requestURL.indexOf("admin/category") != -1
				|| requestURL.indexOf("admin/item") != -1
				|| requestURL.indexOf("admin/order") != -1) {
			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;

	}

	/**
	 * 一般会員 リクエストURLがチェック対象であるかを判定
	 * 
	 * @param requestURL リクエストURL
	 * @param contextPath コンテキストパス名
	 * @return true：チェック対象、false：チェック対象外
	 */
	public static boolean isURLForClient(String requestURL, String contextPath) {

		boolean isCheckURLOK = false;
		if (URLCheck.isURLForStaticFile(requestURL)
				|| requestURL.endsWith(contextPath + "/")
				|| requestURL.endsWith("/login")
				|| requestURL.indexOf("client") != -1
				|| requestURL.indexOf("admin") == -1
				|| requestURL.endsWith("/logout")) {
			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;

	}

	/**
	 *  未ログイン、非会員 リクエストURLがチェック対象であるかを判定
	 * 
	 * @param requestURL リクエストURL
	 * @param contextPath コンテキストパス名
	 * @return true：チェック対象、false：チェック対象外
	 */
	public static boolean isURLForNonLogin(String requestURL, String contextPath) {

		boolean isCheckURLOK = false;
		if (!URLCheck.isURLForStaticFile(requestURL)
				&& !requestURL.endsWith("/login")
				&& !requestURL.endsWith(contextPath + "/")
				&& requestURL.indexOf("/client/item/list/") == -1
				&& requestURL.indexOf("/client/item/detail/") == -1
				&& requestURL.indexOf("/client/user/delete/") == -1
				&& !requestURL.endsWith("/client/user/regist/input/init")
				&& !requestURL.endsWith("/client/user/regist/input")
				&& !requestURL.endsWith("/client/user/regist/input/check")
				&& !requestURL.endsWith("/client/user/regist/check")
				&& !requestURL.endsWith("/client/user/regist/check/back")
				&& !requestURL.endsWith("/client/user/regist/complete")) {
			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;

	}

	/**
	 * カテゴリ一覧用 リクエストURLがチェック対象であるかを判定
	 *
	 * @param requestURL リクエストURL
	 * @return true：チェック対象、false：チェック対象外
	 */
	public static boolean isURLForMakeCategoryList(String requestURL) {

		boolean isCheckURLOK = false;
		if ((!URLCheck.isURLForStaticFile(requestURL)
				&& requestURL.indexOf("/adminmenu") == -1)
				&& (requestURL.endsWith("/")
						|| requestURL.indexOf("/item/list") != -1
						|| requestURL.indexOf("/item/detail") != -1
						|| requestURL.indexOf("/admin/item/regist/input") != -1
						|| requestURL.indexOf("/admin/item/update/input") != -1
						|| requestURL.indexOf("/client/basket") != -1
						|| requestURL.indexOf("/client/order/address") != -1
						|| requestURL.indexOf("/client/order/payment/input") != -1
						|| requestURL.indexOf("/order/list") != -1
						|| requestURL.indexOf("/client/order/check") != -1
						|| requestURL.indexOf("/order/detail") != -1
						|| requestURL.indexOf("/client/order/complete") != -1
						|| requestURL.indexOf("/client/user/detail") != -1
						|| requestURL.indexOf("/client/user/regist") != -1
						|| requestURL.indexOf("/client/user/update") != -1
						|| requestURL.indexOf("/client/user/delete") != -1)) {

			// URLのリクエスト先がフィルタ実行対象である場合
			isCheckURLOK = true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			isCheckURLOK = false;
		}
		return isCheckURLOK;

	}
}
