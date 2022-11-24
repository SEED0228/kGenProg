rm -r json20221117-1
mkdir json20221117-1
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization Zoltar --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_z.json
cp stDev.csv json20221117-1/qs_z.csv
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization Ochiai --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_o.json
cp stDev.csv json20221117-1/qs_o.csv
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization Ample --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_a.json
cp stDev.csv json20221117-1/qs_a.csv
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization Tarantula --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_t.json
cp stDev.csv json20221117-1/qs_t.csv
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization DStar --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_d.json
cp stDev.csv json20221117-1/qs_d.csv
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort03/src/example/QuickSort.java -t example/QuickSort03/src/example/QuickSortTest.java --history-record --fault-localization Jaccard --max-generation 1000 --time-limit 10000
cp kgenprog-out/history.json json20221117-1/qs_j.json
cp stDev.csv json20221117-1/qs_j.csv
