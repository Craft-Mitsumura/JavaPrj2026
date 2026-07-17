package jp.co.sss.shop.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 注文情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "orders")
public class Order {
	/**
	 * 注文ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orders_gen")
	@SequenceGenerator(name = "seq_orders_gen", sequenceName = "seq_orders", allocationSize = 1)
	private Integer id;

	/**
	 * 送付先郵便番号
	 */
	@Column
	private String postalCode;

	/**
	 * 送付先住所
	 */
	@Column
	private String address;

	/**
	 * 送付先宛名
	 */
	@Column
	private String name;

	/**
	 * 送付先電話番号
	 */
	@Column
	private String phoneNumber;

	/**
	 * 支払方法
	 */
	@Column
	private Integer payMethod;

	/**
	 * 注文日付
	 */
	@Column(insertable = false)
	private Date insertDate;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	/**
	 * 注文商品リスト
	 */
	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItemsList;

	/**
	 * 注文IDの取得
	 * @return 注文ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 注文IDのセット
	 * @param id 注文ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * お届け先郵便番号の取得
	 * @return お届け先郵便番号
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * お届け先郵便番号のセット
	 * @param postalCode お届け先郵便番号
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * お届け先住所の取得
	 * @return お届け先住所
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * お届け先住所のセット
	 * @param address お届け先住所
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * お届け先氏名の取得
	 * @return お届け先氏名
	 */
	public String getName() {
		return name;
	}

	/**
	 * お届け先氏名のセット
	 * @param name お届け先氏名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * お届け先電話番号の取得
	 * @return お届け先電話番号
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * お届け先電話番号のセット
	 * @param phoneNumber お届け先電話番号
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 支払方法の取得
	 * @return 支払方法
	 */
	public Integer getPayMethod() {
		return payMethod;
	}

	/**
	 * 支払い方法のセット
	 * @param payMethod 支払方法
	 */
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Date getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * 会員エンティティの取得
	 * @return 会員エンティティ
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 会員エンティティのセット
	 * @param user 会員エンティティ
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 注文IDに紐づく注文商品エンティティのリストを取得
	 * @return 注文商品エンティティのリスト
	 */
	public List<OrderItem> getOrderItemsList() {
		return orderItemsList;
	}

	/**
	 * 注文商品エンティティのリストをセット
	 * @param orderItemsList 注文商品エンティティのリスト
	 */
	public void setOrderItemsList(List<OrderItem> orderItemsList) {
		this.orderItemsList = orderItemsList;
	}

}
