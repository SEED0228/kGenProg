package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
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
public class VariantSerializer implements JsonSerializer<Variant> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  private static int levenshteinDistance( String s1, String s2 ) {
    return dist( s1.toCharArray(), s2.toCharArray() );
  }

  private static int dist( char[] s1, char[] s2 ) {

    // memoize only previous line of distance matrix
    int[] prev = new int[ s2.length + 1 ];

    for( int j = 0; j < s2.length + 1; j++ ) {
      prev[ j ] = j;
    }

    for( int i = 1; i < s1.length + 1; i++ ) {

      // calculate current line of distance matrix
      int[] curr = new int[ s2.length + 1 ];
      curr[0] = i;

      for( int j = 1; j < s2.length + 1; j++ ) {
        int d1 = prev[ j ] + 1;
        int d2 = curr[ j - 1 ] + 1;
        int d3 = prev[ j - 1 ];
        if ( s1[ i - 1 ] != s2[ j - 1 ] ) {
          d3 += 1;
        }
        curr[ j ] = Math.min( Math.min( d1, d2 ), d3 );
      }

      // define current line of distance matrix as previous
      prev = curr;
    }
    return prev[ s2.length ];
  }


  /**
   * シリアライズを行う.<br>
   *
   * @param variant シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {

    final int generationNumber = variant.getGenerationNumber()
        .get();
    final String fitness = variant.getFitness()
        .toString();
    final Patch patch = patchGenerator.exec(variant);

    final JsonObject serializedVariant = new JsonObject();

    int distValue = 0;
    if(variant.isBuildSucceeded()) {
      for(GeneratedAST<ProductSourcePath> generatedAST:variant.getGeneratedSourceCode().getProductAsts()) {
        try {
          String code = Files.readString(Path.of("ans/" + Paths.get(generatedAST.getSourcePath().toString())));
          distValue += levenshteinDistance(generatedAST.getSourceCode(), code);
        } catch(IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    else {
      distValue = -1;
    }

    serializedVariant.addProperty("id", variant.getId());
    serializedVariant.addProperty("generationNumber", generationNumber);
    serializedVariant.addProperty("selectionCount", variant.getSelectionCount());
    serializedVariant.addProperty("fitness", fitness);
    serializedVariant.addProperty("levenshteinDistance", distValue);
    serializedVariant.addProperty("isBuildSuccess", variant.isBuildSucceeded());
    serializedVariant.addProperty("isSyntaxValid", variant.isSyntaxValid());
    serializedVariant.addProperty("sourceCode", variant.getGeneratedSourceCode().getProductAsts().size() != 0 ? variant.getGeneratedSourceCode().getProductAsts().get(0).getSourceCode() : "NaN");
//    serializedVariant.add("codes", context.serialize(variant.getGeneratedSourceCode().getProductAsts()));
    serializedVariant.add("bases", context.serialize(variant.getGene()
        .getBases()));
    serializedVariant.add("patch", context.serialize(patch));
    serializedVariant.add("operation", context.serialize(variant.getHistoricalElement()));
    serializedVariant.add("testSummary", context.serialize(variant.getTestResults()));
    serializedVariant.add("suspiciousnesses", context.serialize(variant.getSuspiciousnesses()));

    return serializedVariant;
  }
}
