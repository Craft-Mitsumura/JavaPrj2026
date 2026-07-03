package jp.co.sss.shop.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * お気に入り情報のエンティティクラス
 * 
 * @author 田中（チームF）
 */
@Entity
@Table(name = "favorites")
public class Favorite {

	/**
	 * お気に入りID
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_favorites_gen")
    @SequenceGenerator(
        name = "seq_favorites_gen",
        sequenceName = "seq_favorites",
        allocationSize = 1
    )
    private Integer id;

    /**
     * 会員情報
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * 商品情報
     */
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    /**
     * 削除フラグ 0:未削除、1:削除済み
     */
    @Column(insertable = false)
    private Integer deleteFlag;

    /**
     * 登録日時
     */
    @Column(insertable = false, updatable = false)
    private Date insertDate;

    /**
     * お気に入りIDの取得
     * @return　お気に入りID
     */
    public Integer getId() {
        return id;
    }

    /**
     * お気に入りIDのセット
     * @param id お気に入りID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * ユーザIDの取得
     * @return　ユーザID
     */
    public User getUser() {
        return user;
    }

    /**
     * ユーザIDのセット
     * @param user ユーザID
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 商品IDの取得
     * @return　商品ID
     */
    public Item getItem() {
        return item;
    }

    /**
     * 商品IDのセット
     * @param item　商品ID
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * 削除フラグの取得
     * @return　削除フラグ
     */
    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 削除フラグのセット
     * @param deleteFlag　削除フラグ
     */
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /**
     * お気に入り登録日付の取得
     * @return　お気に入り登録日付
     */
    public Date getInsertDate() {
        return insertDate;
    }

    /**
     * お気に入り登録日付のセット
     * @param insertDate　お気に入り登録日付
     */
    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }
}

