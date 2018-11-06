package jp.kusumotolab.kgenprog.project.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

/**
 * A class loader that loads classes from in-memory data.
 * 
 * @see https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
 */
public class MemoryClassLoader extends URLClassLoader {

  public MemoryClassLoader() throws MalformedURLException {
    this(new URL[] {});
  }

  public MemoryClassLoader(URL[] urls) {
    super(urls);
  }

  /**
   * クラス定義を表すMap． クラス名とバイト配列のペアを持つ．
   */
  private final Map<FullyQualifiedName, byte[]> definitions = new HashMap<>();

  /**
   * メモリ上のバイト配列をクラス定義に追加する．
   * 
   * @param name 定義するクラス名
   * @param bytes 追加するクラス定義
   */
  public void addDefinition(final FullyQualifiedName name, final byte[] bytes) {
    definitions.put(name, bytes);
  }


  @Override
  public Class<?> loadClass(final String name) throws ClassNotFoundException {
    return loadClass(new TargetFullyQualifiedName(name));
  }

  public Class<?> loadClass(final FullyQualifiedName name) throws ClassNotFoundException {
    return loadClass(name, false);
  }

  @Override
  public Class<?> loadClass(final String name, final boolean resolve)
      throws ClassNotFoundException {
    return loadClass(new TargetFullyQualifiedName(name), resolve);
  }

  /**
   * クラスロード． メモリ上のバイト配列のクラス定義を優先で探し，それがなければファイルシステム上の.classファイルからロードを行う．
   */
  public Class<?> loadClass(final FullyQualifiedName name, final boolean resolve)
      throws ClassNotFoundException {
    final byte[] bytes = definitions.get(name);
    if (bytes != null) {
      try {
        return defineClass(name.toString(), bytes, 0, bytes.length);
      } catch (final LinkageError e) {
        // クラスのロードに失敗した，可能性はバイナリが不正か，二重ロード．

        // 既にロードされているクラスを探してみる（二重ロードの可能性を考える）
        final Class<?> clazz = findLoadedClass(name.toString());

        // それでも無理ならおそらくバイナリ不正っぽい
        if (null == clazz) {
          throw e;
        }
      }
    }
    return super.loadClass(name.toString(), resolve);
  }

}
