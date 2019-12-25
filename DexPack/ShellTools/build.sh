
rm app-test.apk

python getDex.py

java -jar DexPackTool.jar
cp files/classes.dex .

# exchange dex
aapt r app-debug.apk classes.dex
aapt a app-debug.apk classes.dex

cp app-debug.apk app-test.apk

#sign
./resign.sh

adb install app-test.apk
