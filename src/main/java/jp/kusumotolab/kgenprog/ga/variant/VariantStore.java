package jp.kusumotolab.kgenprog.ga.variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation.Input;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * kGenProg が生成する Variant を生成したり保持したりするクラス
 */
public class VariantStore {

  private final Configuration config;
  private final Strategies strategies;
  private final Variant initialVariant;
  private List<Variant> currentVariants;
  private final List<Variant> allVariants;
  private List<Variant> generatedVariants;
  private final List<Variant> foundSolutions;
  private final OrdinalNumber generation;
  private final AtomicLong variantCounter;

  /**
   * @param config 設定
   * @param strategies 個体の生成などをするストラテジー群
   */
  public VariantStore(final Configuration config, final Strategies strategies) {
    this.config = config;
    this.strategies = strategies;

    variantCounter = new AtomicLong();
    generation = new OrdinalNumber(0);
    initialVariant = createInitialVariant();
    currentVariants = Collections.singletonList(initialVariant);
    if (config.getNeedHistoricalElement()) {
      allVariants = new LinkedList<>();
      allVariants.add(initialVariant);
    } else {
      allVariants = null;
    }
    generatedVariants = new ArrayList<>();
    foundSolutions = new ArrayList<>();

    // 最後に次の世代番号に進めておく
    generation.incrementAndGet();
  }

  /**
   * 個体を生成する
   *
   * @param gene 遺伝子情報
   * @param element 生成過程の記録
   * @return 生成された個体
   */
  public Variant createVariant(final Gene gene, final HistoricalElement element) {
    final GeneratedSourceCode sourceCode = strategies.execSourceCodeGeneration(this, gene);
    return createVariant(gene, sourceCode, element);
  }

  /**
   * @return 初期個体を返す
   */
  public Variant getInitialVariant() {
    return initialVariant;
  }

  /**
   * @return 現在の世代数をを返す
   */
  public OrdinalNumber getGenerationNumber() {
    return generation;
  }

  /**
   * @return 見つけた解の個数を返す
   */
  public OrdinalNumber getFoundSolutionsNumber() {
    return new OrdinalNumber(foundSolutions.size());
  }

  /**
   * @return 現世代の個体のリスト
   */
  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }

  /**
   * @return 生成された個体のリスト
   */
  public List<Variant> getGeneratedVariants() {
    return generatedVariants;
  }

  /**
   * @return 今まで生成した全ての個体のリスト
   */
  public List<Variant> getAllVariants() {
    return allVariants;
  }

  /**
   * @return 見つけた正解個体のリスト
   */
  public List<Variant> getFoundSolutions() {
    return foundSolutions;
  }

  /**
   * @param maxNumber 必要な正解個体の数
   * @return 見つけた正解個体のリスト
   */
  public List<Variant> getFoundSolutions(final int maxNumber) {
    final int length = Math.min(maxNumber, foundSolutions.size());
    return foundSolutions.subList(0, length);
  }

  /**
   * 引数の要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see #addGeneratedVariants(Collection)
   */
  // * @see addNextGenerationVariant(Variant)
  public void addGeneratedVariants(final Variant... variants) {
    addGeneratedVariants(Arrays.asList(variants));
  }

  /**
   * リストの要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see #addGeneratedVariant(Variant)
   */
  public void addGeneratedVariants(final Collection<? extends Variant> variants) {
    variants.forEach(this::addGeneratedVariant);
  }

  /**
   * 引数を次世代のVariantとして追加する {@code variant.isCompleted() == true}
   * の場合，foundSolutionとして追加され次世代のVariantには追加されない
   *
   * @param variant
   */
  public void addGeneratedVariant(final Variant variant) {

    if (config.getNeedHistoricalElement()) {
      allVariants.add(variant);
    }
    if (variant.isCompleted()) {
      foundSolutions.add(variant);
    } else {
      generatedVariants.add(variant);
    }
  }

  /**
   * VariantSelectionを実行し世代交代を行う
   *
   * currentVariantsおよびgeneratedVariantsから次世代のVariantsを選択し，それらを次のcurrentVariantsとする
   * また，generatedVariantsをclearする
   */
  public void proceedNextGeneration() {

    final List<Variant> nextVariants =
        strategies.execVariantSelection(currentVariants, generatedVariants);
    nextVariants.forEach(Variant::incrementSelectionCount);
    generation.incrementAndGet();

    currentVariants = nextVariants;
    generatedVariants = new ArrayList<>();
  }

  private Variant createInitialVariant() {
    final GeneratedSourceCode sourceCode =
        strategies.execASTConstruction(config.getTargetProject());
    final HistoricalElement newElement;
    if (config.getNeedHistoricalElement()) {
      newElement = new OriginalHistoricalElement();
    } else {
      newElement = null;
    }
    return createVariant(new Gene(Collections.emptyList()), sourceCode, newElement);
  }

  private Variant createVariant(final Gene gene, final GeneratedSourceCode sourceCode,
      final HistoricalElement element) {
    final LazyVariant variant = new LazyVariant(variantCounter.getAndIncrement(), generation.get(),
        gene, sourceCode, element);
    final Single<Variant> variantSingle = Single.just(variant)
        .cast(Variant.class)
        .cache();

    final Single<TestResults> resultsSingle =
        sourceCode.shouldBeTested() ? strategies.execAsyncTestExecutor(variantSingle)
            .cache() : Single.just(EmptyTestResults.instance);
    variant.setTestResultsSingle(resultsSingle);

    final Single<Fitness> fitnessSingle = Single
        .zip(variantSingle, resultsSingle,
            (v, r) -> strategies.execSourceCodeValidation(new Input(gene, sourceCode, r)))
        .cache();
    variant.setFitnessSingle(fitnessSingle);

    final Single<List<Suspiciousness>> suspiciousnessListSingle = Single
        .zip(variantSingle, resultsSingle,
            (v, r) -> strategies.execFaultLocalization(sourceCode, r))
        .cache();
    variant.setSuspiciousnessListSingle(suspiciousnessListSingle);

    variant.subscribe();

    return variant;
  }
}
