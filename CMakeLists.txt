cmake_minimum_required(VERSION 3.20)
project(SPL_Client_hw3)

set(CMAKE_CXX_STANDARD 11)

set(Boost_INCLUDE_DIR C:/cygwin64/usr/include)

find_package(Boost REQUIRED COMPONENTS system)
include_directories(${Boost_INCLUDE_DIRS})

add_executable(SPL_Client_hw3 main.cpp connectionHandler.h connectionHandler.cpp)
target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})
