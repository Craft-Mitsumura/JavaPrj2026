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
 * ランキングエンティティ
 * @author 小松原愛
 */

@Entity
@Table(name = "rankings")
////  1. 月別の全体売上ランキング用クエリ
//	@NamedQuery(name = "findBySalesMonthNamedQuery", query="SELECT i.items.id, SUM(i.total) FROM Rankings i "
//			+ "WHERE i.salesMonth = :salesMonth GROUP BY i.items.id ORDER BY SUM(i.total) DESC")
//	//  2. カテゴリーでさらに絞り込む売上ランキング用クエリ（商品IDのみでグループ化するように修正）
//	@NamedQuery(name = "findBySalesMonthAndCategoryNamedQuery", query = "SELECT i.items.id, SUM(i.total) FROM Rankings i "
//			+ "WHERE i.salesMonth = :salesMonth AND i.categories.id = :categoryId GROUP BY i.items.id ORDER BY SUM(i.total) DESC")

public class Rankings {

	/**
	 * ランキングID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rankings_gen")
	@SequenceGenerator(name = "seq_rankings_gen", sequenceName = "seq_rankings", allocationSize = 1)
	private Integer id;

	/**
	 * 売上月
	 */
	@Column(name = "SALES_MONTH")
	private Date salesMonth;

	/**
	 * トータル
	 */
	@Column
	private Integer total;

	/**
	 * 商品
	 */
	@ManyToOne
	@JoinColumn(name = "ITEMS_ID", referencedColumnName = "ID")
	private Item item;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getSalesMonth() {
		return salesMonth;
	}

	public void setSalesMonth(Date salesMonth) {
		this.salesMonth = salesMonth;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
