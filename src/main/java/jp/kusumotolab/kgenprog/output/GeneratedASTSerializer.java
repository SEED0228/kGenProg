package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * Variantをシリアライズするクラス.<br>
 *
 * <table border="1">
 * <thead>
 * <tr>
 * <td>キー</td>
 * <td>説明</td>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td>id</td>
 * <td>ID</td>
 * </tr>
 *
 * <tr>
 * <td>generationNumber</td>
 * <td>生成された世代</td>
 * </tr>
 *
 * <tr>
 * <td>selectionCount</td>
 * <td>次世代のバリアントに選ばれた回数</td>
 * </tr>
 *
 * <tr>
 * <td>fitness</td>
 * <td>適応度．Nanのときは-1に変換する．</td>
 * </tr>
 *
 * <tr>
 * <td>isBuildSuccess</td>
 * <td>ビルド結果</td>
 * </tr>
 *
 * <tr>
 * <td>isSyntaxValid</td>
 * <td>文法的に正しいか</td>
 * </tr>
 *
 * <tr>
 * <td>bases</td>
 * <td>塩基の配列</td>
 * </tr>
 *
 * <tr>
 * <td>patch</td>
 * <td>0世代目のバリアントとの差分</td>
 * </tr>
 *
 * <tr>
 * <td>operation</td>
 * <td>適用した操作の配列</td>
 * </tr>
 *
 * <tr>
 * <td>testSummary</td>
 * <td>テスト結果</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 * @see BaseSerializer
 * @see PatchSerializer
 * @see HistoricalElementSerializer
 * @see MutationHistoricalElementSerializer
 * @see TestResultSerializer
 */
public class GeneratedASTSerializer implements JsonSerializer<GeneratedAST<ProductSourcePath>> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  /**
   * シリアライズを行う.<br>
   *
   * @param generatedAST シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final GeneratedAST<ProductSourcePath> generatedAST, final Type type,
                               final JsonSerializationContext context) {

    final JsonObject serializedGeneratedAST = new JsonObject();

    serializedGeneratedAST.addProperty("path", generatedAST.getSourcePath().toString());
    serializedGeneratedAST.addProperty("code", generatedAST.getSourceCode());


    return serializedGeneratedAST;
  }
}
