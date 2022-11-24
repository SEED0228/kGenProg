package example;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class QuickSortTest {

  @Test(timeout = 100)
  public void test01() {
    int[] array = {1, 2, 3};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test02() {
    int[] array = {1, 3, 2};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test03() {
    int[] array = {2, 1, 3};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test04() {
    int[] array = {2, 3, 1};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test05() {
    int[] array = {3, 1, 2};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test06() {
    int[] array = {3, 2, 1};
    int[] expected = {1, 2, 3};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test(timeout = 100)
  public void test07() {
    int[] array = {6, 5, 9, 4, 1, 7, 2, 3, 8, 1};
    int[] expected = {1, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    new QuickSort().quicksort(array);
    assertArrayEquals(expected, array);
  }

  @Test
  public void testZ01() {
    assertEquals(9, new QuickSort().close_to_zero(10));
  }

  @Test
  public void testZ02() {
    assertEquals(99, new QuickSort().close_to_zero(100));
  }

  @Test
  public void testZ03() {
    assertEquals(0, new QuickSort().close_to_zero(0));
  }

  @Test
  public void testZ04() {
    assertEquals(-9, new QuickSort().close_to_zero(-10));
  }

}
