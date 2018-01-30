//
// Created by huaixi on 2018/1/27.
//
#include <string>
#include "hello.h"

using namespace std;

Hello::Hello() {}

string Hello::getHello() {
    return "Hello Huaixi";
}

int Hello::sum(int a, int b) {
    return a + b;
}
