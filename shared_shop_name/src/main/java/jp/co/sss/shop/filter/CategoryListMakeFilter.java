package jp.co.sss.shop.filter;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jp.co.sss.shop.bean.CategoryBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.URLCheck;

/**
 *  カテゴリ一覧取得用フィルター
 *
 * @author System Shared
 */

public class CategoryListMakeFilter extends HttpFilter {

	/**
	 * カテゴリリポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	/**
	 * フィルタの初期化時にこのフィルタ内のAutowiredを実行する
	 *
	 * @param filterConfig フィルタの初期化時、コンテナーからフィルターに情報を渡すためのフィルター構成オブジェクト
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// このフィルタークラスの@Autowiredインジェクションを処理
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// リクエストURLを取得
		String requestURL = request.getRequestURI();
		if (URLCheck.isURLForMakeCategoryList(requestURL)) {

			// カテゴリ情報を全件検索
			List<Category> categoryList = categoryRepository
					.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);

			// エンティティ内の検索結果をJavaBeansにコピー
			List<CategoryBean> categoryBeanList = beanTools.copyEntityListToCategoryBeanList(categoryList);

			//リクエストスコープに検索結果を保存
			request.setAttribute("categories", categoryBeanList);
		}
		chain.doFilter(request, response);
	}


}
