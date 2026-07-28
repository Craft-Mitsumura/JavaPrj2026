package printerBug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class SVF {
  /**
   * 帳票出力
   * @param args[0] accessToken 例：77eb1e8fe50a0b24f60c8ef5efg131096fbc4329e2f089f82d30f734f2c9a7a07
   * @param args[1] printerId 例："0693i030-83af-47dc-82f8-b1e2065209e6"などのユニークID
   * @param args[2] formFilePath 例：form/Test/sample_ja.xml
   * @param args[3] dataFilePath 例：/WebAPISample/sample_ja.csv
   * @param args[4] resourceFilePath 例：/WebAPISample/logo.png
   * @throws InterruptedException 
   */
  public static void main(String[] args) throws InterruptedException {
    String accessToken = null;
    try {
      // obtain access token programmatically from TokenCreater
      accessToken = TokenCreater.fetchAccessToken();
      System.out.println("Fetched access token: " + (accessToken == null ? "(null)" : accessToken.substring(0, Math.min(8, accessToken.length())) + "..."));
    } catch (Exception e) {
      System.out.println("Could not fetch access token: " + e.getMessage());
      e.printStackTrace();
      return;
    }
    String printerId = "4143cad0-1b3c-4aeb-b2da-00ee91855bda"; //  325_9440e printerid printer id from the svf cloud
    String formFilePath =   "form/Sample/納品書/SVF納品書.xml"; // form/ SVF Cloud Web Designer ko xml file path halna baki cha 
    String dataFilePath = "C:\\Users\\CIS_SAGAR\\Downloads\\test.csv";  //("c:\\";) we have to give the local c drive path;
   // String resourceFilePath = args[4]; // optional if the company have logo then it will required to set the path;

    
    boolean exists = java.nio.file.Files.exists(java.nio.file.Paths.get(dataFilePath));

    System.out.println("File exists: " + exists);
    
    // 帳票出力                         , resourceFilePath (if logo is needed)
    String location = SVF.print(accessToken, printerId, formFilePath, dataFilePath);
    if (location != null) {
      System.out.println("location=\n" + location);
    } else {
      System.out.println("print error.");
      return;
    }
    
    // 印刷が完了するくらいの時間を設定
    Thread.sleep(5000);

    // 印刷状況の取得
    String printStatus = SVF.retrievePrintStatus(accessToken, location);
    System.out.println("printStatus=\n" + printStatus);
  }

 
//APIを利用するためのベースドメイン
private static final String API_ENDPOINT = "https://api.svfcloud.com/";

//【修正】アクセストークンを取得するためのエンドポイント）
private static final String TOKEN_URI = "oauth2/token";

//【追加】帳票の成果物を操作する実際のエンドポイント（例: v1/artifacts）
private static final String ARTIFACTS_URI = "v1/artifacts";

private static final String CRLF = "\r\n"; // 改行


  /**
   * 帳票出力
   * @param accessToken
   * @param printerId
   * @param formFilePath
   * @param dataFilePath
   * @param resourceFilePath
   * @return
   */      //, String resourceFilePath optional this is the logo path 
  public static String print(String accessToken, String printerId, String formFilePath, String dataFilePath) {
    HttpURLConnection conn = null;
    String location = null;
    try {
      String boundary = Long.toString(System.currentTimeMillis());
      conn = createPostConnection(ARTIFACTS_URI, "multipart/form-data", boundary, accessToken);

      OutputStream outputStream = conn.getOutputStream();
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
      // リクエストデータの作成
      writeFormData(writer, boundary, "name", "WingArc");// 文書名を指定
      writeFormData(writer, boundary, "source", "CSV");// 帳票データタイプを指定します。"CSV"固定で指定してください
      if (printerId != null) {
        writeFormData(writer, boundary, "printer", printerId);// 出力先プリンターのID
      }
      writeFormData(writer, boundary, "defaultForm", formFilePath);// WebDesigner上のパスを指定します。"form/"で始まるパスを指定します。フォーム名の最後は".xml"を付与してください。

//      // 以下は必要に応じて設定してください。
//      writeFormData(writer, boundary, "password", "user");// PDFに対するパスワードを指定
//      writeFormData(writer, boundary, "inputTray", "3");// 給紙トレイを指定
//      writeFormData(writer, boundary, "pdfPermPass", "owner");// 権限パスワードを指定
//      writeFormData(writer, boundary, "pdfPermPrint", "high");// PDF印刷許可を指定
//      writeFormData(writer, boundary, "pdfPermModify", "assembly");// PDF変更許可を指定
//      writeFormData(writer, boundary, "pdfPermCopy", "true");// PDFコピー許可を指定
//      writeFormData(writer, boundary, "redirect", "false");// リダイレクト動作を指定
//      writeFormData(writer, boundary, "useEudc", "false");// 外字ファイル利用を指定
//      writeFormData(writer, boundary, "adjust", "10.0,10.0");// 印字位置調整を指定 例）adjust=,0.2 （Y方向のみ指定） adjust=-0.1, （X方向のみ指定）
//      writeFormData(writer, boundary, "copies", "3");// 印刷部数を指定
//      writeFormData(writer, boundary, "source", "CSV");
 //     writeFormData(writer, boundary, "printer", printerId);
      // writeFormData(writer, boundary, "defaultForm", formFilePath); 
      
      // CSVデータ
      File datafile = new File(dataFilePath);                       //printgarne csv data source ko name expect gareako huncha 
      																//तिम्रो SVF Web Designer को XML form ले CSV data source को नाम के expect गरेको छ?
      // field name should not be URL-encoded; the server expects the data field name (e.g. data/発注書)
      writeFileData(writer, outputStream, boundary, "data/発注書", datafile.getName(), datafile);// CSVファイル

      // イメージデータ uncomment when logo is needed 
//      File resourceFile = new File(resourceFilePath);
//      writeFileData(writer, outputStream, boundary, URLEncoder.encode("resource/logo.png", "UTF-8"), resourceFile.getName(), resourceFile);// イメージファイル
//      
      writer.append("--").append(boundary).append("--").append(CRLF);
      
      // 帳票出力の実行
      writer.close();

      int responseCode = conn.getResponseCode();
      // 印刷の場合には、202、ファイルダウンロードの場合には、303のステータスコードが返ります。
      if (responseCode == HttpURLConnection.HTTP_ACCEPTED/* 202 */ || responseCode == HttpURLConnection.HTTP_SEE_OTHER/* 303 */) {
        System.out.println(String.format("[%d]", responseCode));
        location = conn.getHeaderField("Location");
        // locationには、
        // 印刷の場合->リクエストの結果から印刷状況を確認するURLが入ります。
        // ファイルダウンロードの場合->リクエストの結果からダウンロード先のURLが入ります。
      
      
      } else{
        System.out.println(String.format("[%d][%s]", responseCode, conn.getResponseMessage()));
        // try to print response body for debugging
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
          }
          System.out.println("Response body:\n" + sb.toString());
        } catch (Exception ex) {
          // ignore
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return location;
  }

  /**
   * 印刷状況の取得
   * @param accessToken
   * @param location
   * @return 印刷状況
   */
  public static String retrievePrintStatus(String accessToken, String location) {
    String printerInfo = null;
    HttpURLConnection conn = null;

    try {
      conn = createGetConnection(location, "application/json", accessToken);

      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // リクエストの結果から印刷状況を取り出します。
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
          printerInfo = reader.readLine();
        }
      } else {
        System.out.println(String.format("[%d][%s]", responseCode, conn.getResponseMessage()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return printerInfo;
  }
  
  // if the status will be error hami lay yei code laekhera teslai hold garauna parcha  
  //
  public static String waitPrintComplete(String accessToken, String location) 
	        throws InterruptedException {

	    String status = null;
	    //int retryCount = 0;
     // int maxTry = 60;
	    //yedi maxtry garaune ho bhane 
	    //while(retryCount < maxRetry) {
	    while (true) {

	        status = retrievePrintStatus(accessToken, location);

	     
	        if (status != null && status.contains("completed")) {
	            break;
	        }

	      
	        if (status != null && status.contains("error")) {
	            break;
	        }

	      
	        Thread.sleep(1000);
	     //   maxtry++;
	//    }
	    }
	    return status;
	}


  
  
  
  /**
   * Postリクエスト用HTTPコネクションの作成
   * @param endPoint
   * @param contentType
   * @param boundary
   * @param accessToken
   * @return HttpURLConnection
   * @throws IOException
   */
  private static HttpURLConnection createPostConnection(String endPoint, String contentType, String boundary, String accessToken) throws IOException {
    URL url = null;
    url = new URL(API_ENDPOINT + endPoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setUseCaches(false);
    conn.setAllowUserInteraction(false);
    // Httpリクエストヘッダーの設定
    conn.setRequestProperty("Content-Type", contentType + "; boundary=" + boundary);
    conn.setRequestProperty("Authorization", "Bearer " + accessToken); // token halna baki cha 
    // chunked HTTP streaming mode 
    conn.setChunkedStreamingMode(0);
    return conn;
  }

  
  /**
   * Getリクエスト用HTTPコネクションの作成
   * @param endPoint
   * @param accept
   * @param accessToken
   * @return HttpURLConnection
   * @throws IOException
   */
  private static HttpURLConnection createGetConnection(String endPoint, String accept, String accessToken) throws IOException {
    URL url = null;
    url = new URL(endPoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    // Httpリクエストヘッダーの設定
    conn.setRequestProperty("Accept", accept);
    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
    return conn;
  }

  /**
   * フォームデータの設定
   * @param writer
   * @param boundary
   * @param name
   * @param value
   * @throws IOException
   */
  private static void writeFormData(PrintWriter writer, String boundary, String name, String value) throws IOException {
    writer.append("--").append(boundary).append(CRLF)
    .append("Content-Disposition: form-data; name=\"").append(name)
    .append("\"").append(CRLF)
    .append("Content-Type: text/plain; charset=").append("UTF-8")
    .append(CRLF).append(CRLF).append(value).append(CRLF);
  }

  /**
   * アップロードファイルデータの設定
   * @param writer
   * @param outputStream
   * @param boundary
   * @param name サーバー側で一時保存するディレクトリ名（任意にディレクトリ名を指定してください。）
   * @param value
   * @param file
   * @throws IOException
   */
  private static void writeFileData(PrintWriter writer, OutputStream outputStream, String boundary, String name, String value, File file) throws IOException {
    writer.append("--").append(boundary).append(CRLF)
    .append("Content-Disposition: form-data; name=\"")
    .append(name).append("\"; filename=\"").append(value)
    .append("\"").append(CRLF).append("Content-Type: ")
    .append(getContentTypeName(file)).append(CRLF)
    .append("Content-Transfer-Encoding: binary").append(CRLF)
    .append(CRLF);
    
    writer.flush();
    outputStream.flush();

    try (FileInputStream in = new FileInputStream(file)) {
      int len ;
      byte[] b = new byte[8192];
      while ((len = in.read(b)) != -1) {
        outputStream.write(b, 0, len);
      }
      outputStream.flush();
    }
    writer.append(CRLF);
  }
  
  private static String getContentTypeName(File file) throws IOException {
    String path = file.getPath().toLowerCase();
    if (path.endsWith(".png")) {
      return "image/png";
    } else if (path.endsWith(".jpg")) {
      return "image/jpeg";
    } else if (path.endsWith(".bmp")) {
      return "image/bmp";
    }else if (path.endsWith(".csv")) {
        return "text/csv";
    } else {
      return "application/octet-stream";
    }
  }

}
