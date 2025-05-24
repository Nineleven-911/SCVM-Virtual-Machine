package kt.hairinne.SCVM.VirtualMachine.StackVM

import kt.hairinne.SCVM.prepose.byteListOfInts
import kt.hairinne.SCVM.prepose.getKey
import kt.hairinne.SCVM.prepose.longExpand
import java.io.File

class StackVM(
    val identifier: Long,
    val immediateSize: Short,
    var ptr: Long
) {

    val program: MutableList<List<Byte>> = mutableListOf()
    var stack: MutableList<Long> = mutableListOf()
    var memory: MutableList<Long> = mutableListOf()
    val version = byteListOfInts(0, 0, 1, 0) // 0.01.0

    init {
        // println("Virtual Machine created. Identifier = \"$identifier\".")
        // println("Virtual Machine initialized.")
        checkMapping()
    }

    fun writeByteCode(byteCode: List<Byte>) {
        program += byteCode
        this.ptr += 1
    }

    fun writeByteCodes(byteCodes: List<List<Byte>>) {
        for (byteCode in byteCodes) {
            this.writeByteCode(byteCode)
        }
    }

    fun write(stat: String) { // Single line statement input function
        val byteCode: List<Byte> = parseStatement(stat, immediateSize)
        writeByteCode(byteCode)
    }

    fun write(stat: List<String>) { // Multiple line statement input function
        val byteCodes: MutableList<List<Byte>> = mutableListOf()
        for (statements in stat) {
            if (statements.isEmpty()) {continue}
            val byteCode: List<Byte> = parseStatement(statements, immediateSize)
            byteCodes += byteCode
        }
        writeByteCodes(byteCodes)
    }

    fun run(ptr: Int = this.ptr.toInt()) {
        for (statIndex in ptr until program.size) {
            this.ptr = statIndex.toLong()
            val stmt =  program[statIndex]
            val cmd = stmt[0]
            val argument: MutableList<Long> = mutableListOf()

            // Parse arguments
            for (argIndex in 1 until stmt.size step immediateSize.toInt() / 8) {
                val arg: MutableList<Byte> = mutableListOf()

                for (i in 0 until immediateSize / 8)
                    arg += stmt[argIndex + i]

                argument += arg.toList().longExpand()
            }

            // Executing command
            when (cmd2Byte.getKey(cmd)) {
                "push" -> stack += argument[0]
                "pop" -> stack.removeLast()
                "print" -> {
                    if (stack.isEmpty()) throw RuntimeException("Stack is empty.")
                    println(stack.last())
                }
                "add" -> {
                    if (stack.size < 2) throw RuntimeException("Not enough elements in stack to perform \"add\".")
                    stack += stack.removeLast() + stack.removeLast()
                }
                "dup" -> {
                    if (stack.isEmpty()) throw RuntimeException("Stack is empty.")
                    stack += stack.last()
                }
                "swap" -> {
                    if (stack.size < 2) throw RuntimeException("Not enough elements in stack to perform \"swap\".")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    stack += a; stack += b
                }
            }
            // println("Counter $statIndex: Command: $cmd, Arguments: $argument")
        }
    }

    fun analysisByteCodeCommands(bytes: ByteArray, withFileHeader: Boolean = false): List<List<Byte>> {
        val result: List<List<Byte>> = analysisByteCodes(bytes, immediateSize, withFileHeader, this.version)
        return result
    }

    fun save(filePath: String, headerData: List<Byte>) {
        require(headerData.size == 16) { "Header data must be exactly 16 bytes" }
        val program: MutableList<Byte> = mutableListOf()

        for (i in 0 until this.program.size) {
            for (j in 0 until this.program[i].size) {
                program += this.program[i][j]
            }
        }

        val output: ByteArray = headerData.toByteArray() + program.toList()

        File(filePath).writeBytes(output)
        println("Program saved to file: $filePath")
    }
}