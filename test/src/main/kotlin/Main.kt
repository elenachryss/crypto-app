package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    println("please input a number")
    val input = readln()
    //αυτο μπορει να βγαλει σφαλμα αν βαλουμε string για αυτο βαζουμε try catch
//    val inputAsInt = input.toInt()
    val inputAsInt = try {
        input.toInt()
    } catch (e: NumberFormatException) {
        null
    }

    println("please input your age")
    val mAge = readln()

//    when (mAge) {
//        100 -> println("old enough")
//        else -> println("not old enough")
//
//    }

    val output = when (inputAsInt) {
        null -> "enter a valid number"
        3 -> "three"
        5 -> "five"
        in 4..20 -> "in range"
        else -> "not in range"
    }

//    val isEven = inputAsInt % 2 == 0 // TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
//    println("is the number even?  $isEven" )
    println("output is $output")
}
