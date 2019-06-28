#! /bin/bash

# 安装所有模块
mvn clean install

# 启动接入程序
cd kemao_2
mvn  spring-boot:start

# 启动关注程序
cd ../subscribe
mvn  spring-boot:start

# 启动取消关注程序
cd ../unsubscribe
mvn  spring-boot:start
