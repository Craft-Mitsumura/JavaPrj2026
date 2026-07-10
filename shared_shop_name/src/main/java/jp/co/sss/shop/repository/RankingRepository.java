package jp.co.sss.shop.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Rankings;

@Repository
public interface RankingRepository
        extends JpaRepository<Rankings, Integer> {

    /**
     * ランキングDB検索用
     */
    Rankings findBySalesMonthAndItem_Id(Date salesMonth,Integer itemId);
}
