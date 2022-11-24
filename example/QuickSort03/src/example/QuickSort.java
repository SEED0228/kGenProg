package example;

public class QuickSort {

  public void quicksort(int[] value) {
    int left = 0;
    int right = value.length - 1;
    quicksort(value, left, right);
  }

  /**
   * 無限ループするバグを含むメソッド
   * 
   * @param value
   * @param left
   * @param right
   */
  private void quicksort(int[] value, int left, int right) {

    int i = left;
    int j = right;
    int pivot = value[(left + right) / 2];

    while (true) {
      while (value[i] < pivot)
        j++; // to be "i++"
      while (pivot < value[j])
        j--;
      if (i >= j)
        break;
      swap(value, i, j);
      i++;
      j--;
    }

    if (left < i - 1)
      quicksort(value, left, i - 1);
    if (j + 1 < right)
      quicksort(value, j + 1, right);
  }

  private void swap(int[] value, int i, int j) {
    int tmp = value[i];
    value[i] = value[j];
    value[j] = tmp;
  }

  @SuppressWarnings("unused")
  private void reuse_me() {
    int i = 0;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
  }

  public int close_to_zero(int n) {
    if (n == 0) {
      n++; // bug here
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    return n;
  }

  public int countDown(int n) {
    if (0 <= n) { // bug here
      n--;
    }
    return n;
  }

  // 再利用されるべきメソッド1
  public int doNothing(int n) {
    while (0 < n) {
    }
    return n;
  }

  public int gcd(int a, int b) {

    if (a == 0) {
      return 0; // to be "return b"
    }

    while (b != 0) {
      if (a > b) {
        a = a - b;
      } else {
        b = b - a;
      }
    }

    return a;
  }

  @SuppressWarnings("unused")
  private int reuse_me2(int a, int b) {
    if (a > b) {
      return a;
    } else if (a < b) {
      return b;
    } else {
      return 0;
    }
  }
}
