package jp.co.sss.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.User;

/**
 * usersテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * メールアドレスに該当する会員情報を検索（メールアドレスのみ） Validatorで利用
	 * @param email メールアドレス
	 * @return 会員エンティティ
	 */
	User findByEmail(String email);

	/**
	 * メールアドレスと削除フラグに該当する会員情報を検索 Validatorで利用
	 * @param email メールアドレス
	 * @param deleteFlag 削除フラグ
	 * @return 会員エンティティ
	 */
	User findByEmailAndDeleteFlag(String email, int deleteFlag);

	/**
	 * 会員情報をログインユーザ権限を条件に登録日付降順に取得 (管理者機能で利用)
	 * @param deleteFlag 削除フラグ
	 * @param authority 権限
	 * @param pageable ページング情報
	 * @return 会員エンティティのページオブジェクト
	 */
	@Query("SELECT u FROM User u WHERE u.deleteFlag =:deleteFlag AND u.authority >= :authority ORDER BY u.insertDate DESC,u.id DESC")
	Page<User> findUsersListOrderByInsertDate(
			@Param("deleteFlag") int deleteFlag, @Param("authority") int authority, Pageable pageable);

	/**
	 * 会員IDと削除フラグを条件に検索
	 * @param id 会員ID
	 * @param deleteFlg 削除フラグ
	 * @return 会員エンティティ
	 */
	User findByIdAndDeleteFlag(Integer id, int deleteFlg);

}
