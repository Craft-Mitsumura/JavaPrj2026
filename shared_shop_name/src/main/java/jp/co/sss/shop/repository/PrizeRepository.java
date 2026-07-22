package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Prize;

public interface PrizeRepository extends JpaRepository<Prize, Integer> {

    Prize findFirstByRequiredPointGreaterThanOrderByRequiredPointAsc(Integer point);

    List<Prize> findAllByOrderByIdDesc();
}
