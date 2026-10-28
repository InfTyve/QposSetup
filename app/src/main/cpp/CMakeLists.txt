cmake_minimum_required(VERSION 3.22.1)

project("tyve")

add_library(
        tyve
        SHARED
        hello.cpp
)

find_library(
        log-lib
        log
)

# 将系统库链接到你的本地库
target_link_libraries(
        tyve
        ${log-lib}
)
