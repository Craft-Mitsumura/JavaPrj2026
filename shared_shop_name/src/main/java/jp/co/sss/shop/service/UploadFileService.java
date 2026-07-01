package jp.co.sss.shop.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.co.sss.shop.util.Constant;

/**
 * 画像ファイルのアップロードを行うサービスクラス
 *
 * @author System Shared
 */
@Service
public class UploadFileService {

	/**
	 * ファイルのアップロードを行う処理
	 * @param upfile アップロード対象のファイル
	 * @return 正常にアップロードできた場合:ファイル名 失敗もしくはサイズ0バイトの場合:null
	 */
	public String saveUploadFile(MultipartFile upfile) {

		String imageName = null;

		if (upfile.getSize() > 0) {

			// アップロード対象のファイル名を取得
			imageName = upfile.getOriginalFilename();

			// 現在の日時を「yyyyMMddhhmmss」形式の文字列として取得
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			String date = dateFormat.format(new Date());

			// ファイルのアップロード先を指定
			imageName = date + "_" + imageName;
			Path saveLocation = Paths.get(Constant.FILE_UPLOAD_PATH);
			Path destinationFile = saveLocation.resolve(
					Paths.get(imageName)).toAbsolutePath();

			try {
				//画像ファイルの格納先ディレクトリが存在しているかを確認
				if (!Files.exists(Paths.get(Constant.FILE_UPLOAD_PATH).toAbsolutePath())) {
					Files.createDirectory(Paths.get(Constant.FILE_UPLOAD_PATH).toAbsolutePath());
				}
				InputStream inputStream = upfile.getInputStream();
				// ファイルをコピーする 既に存在している場合は上書き
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return imageName;

	}

}
