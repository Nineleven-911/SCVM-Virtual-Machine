package kt.hairinne.prepose


fun byteListInHex(numberList: List<Byte>) {
    print("[")
    for (i in numberList.indices) {
        print("0x${numberList[i].toString(0x10)}")
        if (i != numberList.size - 1) {
            print(", ")
        }
    }
    println("]")
}
