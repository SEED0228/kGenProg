package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.ExampleAlias.Bin;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class ProjectBuilderTest {

  private final String[] javaExtension = new String[] {"class"};

  @Test
  public void testBuildStringForExample00() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, workPath);

    assertThat(buildResults).isInstanceOf(EmptyBuildResults.class);
    assertThat(buildResults.isBuildFailed).isTrue();
  }

  @Test
  public void testBuildStringForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, workPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(workPath.toFile(), javaExtension, true);
    final Path foo = workPath.resolve(Bin.Foo);
    final Path fooTest = workPath.resolve(Bin.FooTest);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, workPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(workPath.toFile(), javaExtension, true);
    final Path foo = workPath.resolve(Bin.Foo);
    final Path fooTest = workPath.resolve(Bin.FooTest);
    final Path bar = workPath.resolve(Bin.Bar);
    final Path barTest = workPath.resolve(Bin.BarTest);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest, bar, barTest);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, workPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(workPath.toFile(), javaExtension, true);
    final Path foo = workPath.resolve(Bin.Foo);
    final Path fooTest = workPath.resolve(Bin.FooTest);
    final Path bar = workPath.resolve(Bin.Bar);
    final Path barTest = workPath.resolve(Bin.BarTest);
    final Path baz = workPath.resolve(Bin.Baz);
    final Path bazTest = workPath.resolve(Bin.BazTest);
    final Path inner = workPath.resolve(Bin.BazInner);
    final Path staticInner = workPath.resolve(Bin.BazStaticInner);
    final Path anonymous = workPath.resolve(Bin.BazAnonymous);
    final Path outer = workPath.resolve(Bin.BazOuter);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest, bar, barTest, baz, bazTest, inner, staticInner,
            anonymous, outer);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {
    final Path rootPath03 = Paths.get("example/BuildSuccess03");
    final Path rootPath02 = Paths.get("example/BuildSuccess02");

    final Path workPath = rootPath03.resolve("bin");

    // example03のビルドが成功するかテスト
    final TargetProject targetProject03 = TargetProjectFactory.create(rootPath03);
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final Variant variant03 = targetProject03.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode03 = variant03.getGeneratedSourceCode();
    final BuildResults buildResults03 = projectBuilder03.build(generatedSourceCode03, workPath);

    assertThat(buildResults03.isBuildFailed).isFalse();
    assertThat(buildResults03.isMappingAvailable()).isTrue();

    // example02のビルドが成功するかテスト
    final TargetProject targetProject02 = TargetProjectFactory.create(rootPath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final Variant variant02 = targetProject02.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode02 = variant02.getGeneratedSourceCode();
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02, workPath);

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(buildResults02.isMappingAvailable()).isTrue();

    final Collection<File> classFiles =
        FileUtils.listFiles(workPath.toFile(), new String[] {"class"}, true);
    final Path e1 = workPath.resolve(Bin.Foo);
    final Path e2 = workPath.resolve(Bin.FooTest);
    final Path e3 = workPath.resolve(Bin.Bar);
    final Path e4 = workPath.resolve(Bin.BarTest);

    assertThat(classFiles).extracting(File::toPath)
        .containsExactlyInAnyOrder(e1, e2, e3, e4);
  }
}
