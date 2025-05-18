package kt.hairinne.SCVM.VirtualMachine.StackVM

import kt.hairinne.prepose.*

val cmd2Byte: Map<String, Byte> = mapOf(
    "push"  to 0x11,
    "pop"   to 0x12,
    "print" to 0x13,
    "add"   to 0x14,
    "dup"   to 0x15,
    "swap"  to 0x16
)
val cmd2Args: Map<String, Short> = mapOf(
    "push"  to 1,
    "pop"   to 0,
    "print" to 0,
    "add"   to 0,
    "dup"   to 0,
    "swap"  to 0
)

fun checkMapping() {
    for (i in cmd2Byte) {
        if (!cmd2Args.containsKey(i.key)) {
            throw IllegalArgumentException(
                "In file: \"${Thread.currentThread().stackTrace[1].fileName}\", Command ${i.key} has some problems."
            )
        }
    }
}

fun parseStatement(statements: String, immediateSize: Short = 64): List<Byte> {
    val command = statements.lowercase().split(" ")
    val byteCode: MutableList<Byte> = mutableListOf()
    var argsRequire: Short = 0

    for (i in 0 until command.size) {
        if (i == 0) {
            if (cmd2Byte.containsKey(command[i])) {
                byteCode += cmd2Byte.getValue(command[i])
                argsRequire = cmd2Args.getValue(command[i])
            } else {
                throw IllegalArgumentException("Unknown command found while parsing: ${command[i]}")
            }
            if (argsRequire != (command.size - 1).toShort()) {
                throw IllegalArgumentException("Invalid number of arguments for command: ${command[i]}")
            }
        } else {
            try {
                byteCode += command[i].toLong().toByteList(immediateSize)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid argument for command: ${command[i]}, $e")
            }
        }
    }
    return byteCode.toList()
}

fun analysisByteCodes(
    bytes: ByteArray,
    immediateSize: Short,
    withFileHeader: Boolean = false,
    version: List<Byte>): List<List<Byte>> {
    val status = object{
        var parsingStatement = true
        var command: MutableList<Byte> = mutableListOf()
        var counter: Int = 0
    }
    var byteCodesWithoutHeader: ByteArray = bytes
    val result: MutableList<List<Byte>> = mutableListOf()
    var argsRequires: Short = 0

    if (withFileHeader) {

        /* 头数据配置: Header data configuration:
           0-3: 魔数 (Magic Number)
        */

        byteCodesWithoutHeader = bytes.slice(16 /* 0~15 total 16 Bytes */ until bytes.size).toByteArray()
        val header: ByteArray = bytes.slice(0 until 15).toByteArray()

        if (header.slice(0 until 4) != byteListOfInts(0, 0, 0xAE, 0xA0)) {
            throw IllegalAccessException(
                "Invalid file header. Expected: [0x00, 0x00, 0xAE, 0xA0], actual: ${header.slice(0 until 4)}"
            )
        } else if (header.slice(4 until 8) != version) {
            throw IllegalAccessException(
                "VM version mismatch. Expected: $version, actual: ${header.slice(4 until 8)}"
            )
        }
    }
    for (byte in byteCodesWithoutHeader) {
        if (status.counter == argsRequires * (immediateSize / 8)) {
            status.parsingStatement = true
            status.counter = 0
            result += status.command.toList()
            status.command.clear()
        }
        if (status.parsingStatement) {
            if (cmd2Byte.containsValue(byte)) {
                status.command += byte
                argsRequires = cmd2Args.getValue(cmd2Byte.getKey(byte))
                status.parsingStatement = false
            } else {
                throw IllegalArgumentException(
                    "Unknown command found while parsing byte-codes: $byte, index: ${status.counter}."
                )
            }
        } else {
            status.command += byte
            status.counter += 1
        }
    }
    if (status.command.isNotEmpty()) {
        result += status.command.toList()
    }

    return result.toList().slice(1 until result.size) // Remove the first element : []
}
