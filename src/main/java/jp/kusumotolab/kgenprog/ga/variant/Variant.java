package jp.kusumotolab.kgenprog.ga.variant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 各個体に関する様々な情報を保持するクラス
 */
public class Variant {

  private final long id;
  private final int generationNumber;
  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private int selectionCount = 0;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;
  private boolean isUpDatedFitnessValue = false;

  /**
   * コンストラクタ
   *
   * @param id この個体の識別子
   * @param generationNumber この個体が何世代目で生成されたか
   * @param gene この個体がもつべき遺伝子
   * @param generatedSourceCode この個体のソースコード
   * @param testResults この個体のテスト結果
   * @param fitness この個体のの評価値
   * @param suspiciousnesses この個体のソースコードに関する疑惑値
   * @param historicalElement この個体が生成されるまでの過程
   */
  public Variant(final long id, final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final TestResults testResults,
      final Fitness fitness, final List<Suspiciousness> suspiciousnesses,
      final HistoricalElement historicalElement) {
    this.id = id;
    this.generationNumber = generationNumber;
    this.gene = gene;
    this.generatedSourceCode = generatedSourceCode;
    this.testResults = testResults;
    this.fitness = fitness;
    this.suspiciousnesses = suspiciousnesses;
    this.historicalElement = historicalElement;
  }

  /**
   * @return この個体が解かどうか
   */
  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  /**
   * @return この個体の識別子
   */
  public long getId() {
    return id;
  }

  /**
   * @return この個体の AST を生成する上で文法的にた出しいかどうか
   */
  public boolean isSyntaxValid() {
    return generatedSourceCode.isGenerationSuccess();
  }

  /**
   * @return この個体はすでに生成済みかどうか
   */
  public boolean isReproduced() {
    return generatedSourceCode.isReproducedSourceCode();
  }

  /**
   * @return ビルドに成功したかどうか
   */
  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResults.getClass();
  }

  /**
   * @return この個体がビルドにに取り組んだかどうか
   * (AST の生成に失敗した場合・重複している場合はビルドに取り組まない)
   */
  public boolean triedBuild() {
    return generatedSourceCode.shouldBeTested();
  }

  /**
   * @return この個体の世代数
   */
  public OrdinalNumber getGenerationNumber() {
    return new OrdinalNumber(generationNumber);
  }

  /**
   * @return この個体の遺伝子
   */
  public Gene getGene() {
    return gene;
  }

  /**
   * @return この個体のソースコード
   */
  public GeneratedSourceCode getGeneratedSourceCode() {
    return generatedSourceCode;
  }

  /**
   * @return この個体のテスト結果
   */
  public TestResults getTestResults() {
    return testResults;
  }

  /**
   * @return この個体が何回選択されたか
   */
  public int getSelectionCount() {
    return selectionCount;
  }

  /**
   * @return この個体の評価値
   */
  public Fitness getFitness() {
    return fitness;
  }

  /**
   * @return この個体の疑惑値
   */
  public List<Suspiciousness> getSuspiciousnesses() {
    return suspiciousnesses;
  }

  /**
   * @return この個体が生成されるまでの過程
   */
  public HistoricalElement getHistoricalElement() {
    return historicalElement;
  }

  /**
   * この個体が選択されるたびに呼び，選択された回数を更新する
   */
  void incrementSelectionCount() {
    selectionCount++;
  }

  /**
   * 評価値を更新する
   *
   * @param value 更新する評価値
   */
  public void updateFitnessValue(double value) {
    this.isUpDatedFitnessValue = true;
    this.getFitness().setValue(value);
  }

  /**
   * @return 評価値を更新済みかどうか
   */
  public boolean getIsUpDatedFitnessValue() { return this.isUpDatedFitnessValue; }

  public List<Variant> getAllParents() {
    List<Variant> variants = new ArrayList<Variant>();
    for(Variant v: getHistoricalElement().getParents()) {
      for(Variant v2: v.getAllParents()) {
        variants.add(v2);
      }
    }
    variants.add(this);
    variants = new ArrayList<>(new HashSet<>(variants));
    return variants;
  }

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

  public int getLevenshteinDistance() {
    int distValue = 0;
    if(isBuildSucceeded()) {
      for(GeneratedAST<ProductSourcePath> generatedAST: getGeneratedSourceCode().getProductAsts()) {
        try {
          // 通常時
//          String ans = "ans/";
          // real bug
          String ans = "ans/example/real-bugs/Math73/";
          String code = Files.readString(Path.of(ans + Paths.get(generatedAST.getSourcePath().toString())));
          distValue += levenshteinDistance(generatedAST.getSourceCode(), code);
        } catch(IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    else {
      distValue = -1;
    }
    System.out.println(distValue);
    return distValue;
  }
}
