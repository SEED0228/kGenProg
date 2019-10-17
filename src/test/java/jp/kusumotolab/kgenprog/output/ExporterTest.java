package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ExporterTest {

  private Path tempDir;
  private final PatchGenerator patchGenerator = new PatchGenerator();
  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    tempDir = tempFolder.getRoot()
        .toPath();
  }

  /**
   * 1箇所でも差分があるようなVariantを1つ作る
   */
  private Variant createModifiedVariant(final Variant parent) {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location =
        new JDTASTLocation(new ProductSourcePath(basePath, Src.FOO), statement, ast);

    final DeleteOperation operation = new DeleteOperation();
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final TestResults testResults = EmptyTestResults.instance;
    final Base base = new Base(location, operation);
    final Gene gene = new Gene(Collections.singletonList(base));
    final Fitness fitness = new SimpleFitness(1.0d);
    final HistoricalElement historicalElement = new MutationHistoricalElement(parent, base);

    return new Variant(0, 0, gene, code, testResults, fitness, null, historicalElement);
  }

  private Configuration buildConfiguration(final Path outDir) {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final List<Path> productPaths = Collections.singletonList(rootPath.resolve(PRODUCT_NAME));
    final List<Path> testPaths = Collections.singletonList(rootPath.resolve(TEST_NAME));

    return new Configuration.Builder(rootPath, productPaths, testPaths)
        .setOutDir(outDir)
        .setTestTimeLimitSeconds(1)
        .setMaxGeneration(1)
        .setRequiredSolutionsCount(1)
        .build();
  }

  /**
   * 出力先ディレクトリが空でないときにパッチとJSONを出力するか確認する
   */
  @Test
  public void testExporterWithNonEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);
    // 適当なファイルを作る
    final Path childDir = outDir.resolve("childDir");
    final Path childFile = outDir.resolve("child");
    final Path grandChildFile = childDir.resolve("grandChild");
    Files.createDirectory(childDir);
    Files.createFile(childFile);
    Files.createFile(grandChildFile);

    final Configuration config = buildConfiguration(outDir);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config, patchGenerator);
    exporter.export(variantStore);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // 以前に作成したファイル群が存在しないことを確認
    assertThat(childDir).doesNotExist();
    assertThat(childFile).doesNotExist();
    assertThat(grandChildFile).doesNotExist();
    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }

  /**
   * 出力先ディレクトリが空のときにパッチとJSONを出力するか確認する
   */
  @Test
  public void testExporterWithEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);

    final Configuration config = buildConfiguration(outDir);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config, patchGenerator);
    exporter.export(variantStore);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }
}
