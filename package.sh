#!/bin/bash
# 开发环境打包
mvn clean package -Dmaven.test.skip=true -Pdev

# 生产环境打包
#mvn clean package -Dmaven.test.skip=true -Pprod
