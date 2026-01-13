package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun mCalculator(a: Int, b: Double = 20.2): String = "$a $b"

fun function2(fun1: () -> String, fun2: (String) -> String): String = fun1.invoke() + fun2.invoke(" elena")
fun function3(fun1: () -> String, fun2: (String) -> String): String = fun2.invoke(fun1.invoke())
fun main() {

//    println(mCalculator(1))

    val result2 = function2(fun1 = {
        "hello"
    }, fun2 = {
        " world"
    })

    val result3 = function2(fun1 = {
        "hello"
    }, fun2 = { name -> name })

    println(result2)
    println(result3)

    println(function3(fun1 = {
        "elenaaa"
    }, fun2 = { name -> "hello $name" }))
}
