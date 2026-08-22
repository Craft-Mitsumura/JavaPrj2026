package jp.co.sss.shop.controller.admin.ad;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告formの画像アップロードのエラーキャッチ
 *
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession; // 追加

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private HttpSession session; // 追加

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, 
                                         RedirectAttributes redirectAttributes,
                                         HttpServletRequest request,
                                         @ModelAttribute Object form) { // 汎用的に受け取る
        
        // 1. 現在の入力値（form）をセッションに一時保存する
        // ※これが各コントローラーのGETメソッド（registInput等）で再利用されます
        session.setAttribute("form", form); 
        
        // 2. エラーメッセージをセット
        redirectAttributes.addFlashAttribute("errorMessage", "ファイルサイズが大きすぎます。1MB以下の画像を選択してください。");
        
        // 3. 元の画面へリダイレクト
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}