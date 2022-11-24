rm -r json20221124-1
mkdir json20221124-1
java -jar build/libs/kGenProg-1.8.2.jar -r ./ -s example/QuickSort01/src/example/QuickSort.java -t example/QuickSort01/src/example/QuickSortTest.java --history-record --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/qs_z.json
cp stDev.csv json20221124-1/qs_z.csv
java -jar build/libs/kGenProg-1.8.2.jar --max-generation 10000 --history-record --config example/real-bugs/Math02/kgenprog.toml --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/m02_z.json
cp stDev.csv json20221124-1/m02_z.csv
java -jar build/libs/kGenProg-1.8.2.jar --max-generation 10000 --history-record --config example/real-bugs/Math05/kgenprog.toml --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/m05_z.json
cp stDev.csv json20221124-1/m05_z.csv
java -jar build/libs/kGenProg-1.8.2.jar --max-generation 10000 --history-record --config example/real-bugs/Math70/kgenprog.toml --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/m70_z.json
cp stDev.csv json20221124-1/m70_z.csv
java -jar build/libs/kGenProg-1.8.2.jar --max-generation 10000 --history-record --config example/real-bugs/Math73/kgenprog.toml --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/m73_z.json
cp stDev.csv json20221124-1/m73_z.csv
java -jar build/libs/kGenProg-1.8.2.jar --max-generation 10000 --history-record --config example/real-bugs/Math85/kgenprog.toml --fault-localization Zoltar --time-limit 10000
cp kgenprog-out/history.json json20221124-1/m85_z.json
cp stDev.csv json20221124-1/m85_z.csv