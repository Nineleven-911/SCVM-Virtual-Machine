package kt.hairinne.SCVM.VirtualMachine.old.VMTools


val cmd2CodeMap: MutableMap<String, Byte> = mutableMapOf( // Name, Code
    "push" to 0x10,
    "pop" to 0x11,
    "dup" to 0x12,
    "swap" to 0x13,
    "print" to 0x14
)
val cmd2RequiresMap: MutableMap<String, Short> = mutableMapOf( // Name, ArgRequire
    "push" to 1,
    "pop" to 0,
    "dup" to 0,
    "swap" to 0,
    "print" to 0
)


fun List<Byte>.longExpand(): Long {
    require(size <= 8) { "Input byte list size must not exceed 8" }
    var result: Long = 0
    for (byte in this) {
        result = (result shl 8) or (byte.toLong() and 0xFF)
    }
    return result
}

fun Long.toByteList(): List<Byte> {
    var byteList = mutableListOf<Byte>()
    var value = this

    for (i in 0 until 8) {
        byteList.add((value and 0xFF).toByte())
        value = value shr 8
    }
    byteList = byteList.reversed().toMutableList()
    // 去除前导0x0
    while (byteList.size > 1 && byteList[0] == 0x00.toByte()) {
        byteList.removeAt(0)
    }
    return byteList.toList()
}


fun commandParse(cmd: String): List<Byte> {
    val command = cmd.lowercase().split(" ")
    val machineCode: MutableList<Byte> = mutableListOf()
    var argsRequire: Short = 0

    for (i in 0 until command.size) {
        if (i == 0) {
            if (cmd2CodeMap.containsKey(command[i])) {
                machineCode += cmd2CodeMap.getValue(command[i])
                argsRequire = cmd2RequiresMap.getValue(command[i])
            } else {
                throw IllegalArgumentException("Unknown command found while parsing: ${command[i]}")
            }
        } else {
            if (command.size - 1 != argsRequire.toInt()) {
                throw IllegalArgumentException("Invalid number of arguments for command: ${command[i]}")
            }
            try {
                if (command[i].toInt() > 256) {
                    machineCode += command[i].toLong().toByteList()
                } else {
                    machineCode += command[i].toByte()
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid argument for command: ${command[i]}, $e")
            }
        }
    }
    return machineCode.toList()
}

fun multiLinesParse(cmd: String): List<List<Byte>> {
    val commands = cmd.lowercase().lines()
    var machineCode: MutableList<List<Byte>> = mutableListOf()

    for (command in commands) {
        if (command.isEmpty()) {
            continue
        }
        machineCode += commandParse(command).toList()
    }
    return machineCode.toList()
}
