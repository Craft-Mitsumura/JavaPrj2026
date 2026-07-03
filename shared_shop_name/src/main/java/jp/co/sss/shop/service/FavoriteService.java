package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * お気に入りを登録・削除を行うサービスクラス
 * 
 * @author 田中（チームF）
 */
@Service
public class FavoriteService {

	/** お気に入り情報を管理するリポジトリ */
    @Autowired
    private FavoriteRepository favoriteRepository;

    /** 商品情報を管理するリポジトリ */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * お気に入り登録
     * 
     * @param userId　会員ID
     * @param itemId　商品ID
     */
    public void addFavorite(Integer userId, Integer itemId) {

        User user = new User();
        user.setId(userId);

        Item item = itemRepository.findById(itemId).orElse(null);

        Favorite favorite =
                favoriteRepository.findByUserAndItem(user, item);

        if (favorite == null) {

            favorite = new Favorite();
            favorite.setUser(user);
            favorite.setItem(item);

        } else {

            favorite.setDeleteFlag(0);

        }

        favoriteRepository.save(favorite);
    }

    /**
     * お気に入り解除
     * 
     * @param userId　会員ID
     * @param itemId　商品ID
     */
    public void removeFavorite(Integer userId, Integer itemId) {

        User user = new User();
        user.setId(userId);

        Item item = itemRepository.findById(itemId).orElse(null);

        Favorite favorite =
                favoriteRepository.findByUserAndItem(user, item);

        if (favorite != null) {

            favorite.setDeleteFlag(1);

            favoriteRepository.save(favorite);
        }
    }

    /**
     * お気に入り判定
     * 
     * @param  userId　会員ID
     * @param  itemId　商品ID
     * @return お気に入りされていたらtrue、違うならfalse
     */
    public boolean isFavorite(Integer userId, Integer itemId) {

        User user = new User();
        user.setId(userId);

        Item item = itemRepository.findById(itemId).orElse(null);

        Favorite favorite =
                favoriteRepository.findByUserAndItemAndDeleteFlag(
                        user,
                        item,
                        0);

        return favorite != null;
    }

   /**
    * お気に入り一覧取得
    * 
    * @param  userId　会員ID
    * @return お気に入りリスト
    */
    public List<Favorite> getFavoriteList(Integer userId) {

        return favoriteRepository
                .findByUser_IdAndDeleteFlagOrderByInsertDateDesc(
                        userId,
                        0);
    }
}
