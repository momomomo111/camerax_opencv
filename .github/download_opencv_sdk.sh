#!/bin/bash

# curlコマンドのパスを修正
/usr/bin/curl -L https://github.com/opencv/opencv/releases/download/4.10.0/opencv-4.10.0-android-sdk.zip -o /tmp/OpenCV-android-sdk.zip -s

# unzipコマンドのパスを確認（通常は/usr/bin/unzipで問題ありません）
/usr/bin/unzip -q /tmp/OpenCV-android-sdk.zip

# rmコマンドのパスを確認（通常は/bin/rmで問題ありません）
/bin/rm /tmp/OpenCV-android-sdk.zip

# opencv build.gradleファイルを置き換え
cp $GITHUB_WORKSPACE/opencv-manifest/build.gradle $GITHUB_WORKSPACE/OpenCV-android-sdk/sdk/build.gradle
