package kt.hairinne.SCVM.prepose


fun <K, V> Map<K, V>.getKey(value: V): K {
    for ((key, v) in this) {
        if (v == value) {
            return key
        }
    }
    throw NoSuchElementException("No key found for value: $value")
}

fun List<Byte>.longExpand(): Long {
    require(size <= 8) { "Input byte list size must not exceed 8" }
    var result: Long = 0
    for (byte in this) {
        result = (result shl 8) or (byte.toLong() and 0xFF)
    }
    return result
}

fun Long.toByteList(immediateSize: Short = 64): List<Byte> {
    require(immediateSize in 8..64 && immediateSize % 8 == 0) {
        "Immediate size must be between 8 and 64 bits and a multiple of 8"
    }
    var byteList = mutableListOf<Byte>()
    var value = this

    for (i in 0 until 8) {
        byteList.add((value and 0xFF).toByte())
        value = value shr 8
    }

    if (
        byteList.reversed().slice(immediateSize / 8 until byteList.size)
            .any { it != 0.toByte() }
    ) {
        throw IllegalArgumentException("Value exceeds immediate size of $immediateSize bits")
    }

    return byteList.reversed().slice(0 until immediateSize / 8).toList()
}

fun byteListOfInts(vararg ints: Int): List<Byte> {
    val result: MutableList<Byte> = mutableListOf()
    for (i in ints) {
        result += i.toByte()
    }
    return result.toList()
}
