rsync -av ~/Downloads/upay/armeabi/ src/main/jniLibs/armeabi/
rsync -av ~/Downloads/upay/assets/ src/main/assets/
rsync -av ~/Downloads/upay/pics/ src/main/res/drawable-xhdpi/
rm -f libs/Upay*
cp ~/Downloads/upay/Upay_Oversea_Sdk_obfuscated_2.3.1_Sandbox.jar libs/