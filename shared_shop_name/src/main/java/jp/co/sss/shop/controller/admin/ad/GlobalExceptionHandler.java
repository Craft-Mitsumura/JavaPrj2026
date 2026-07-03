package jp.co.sss.shop.controller.admin.ad;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告formの画像アップロードのエラーキャッチ
 *
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        // エラーメッセージをセットして入力画面へリダイレクト
        redirectAttributes.addFlashAttribute("errorMessage", "ファイルサイズが大きすぎます。1MB以下の画像を選択してください。");
        return "redirect:/admin/ad/regist/input";
    }
}