package kt.hairinne.SCVM.VirtualMachine.old

import java.io.File


class RegisterVM (
    val identifier: Long,
    val maxSize: Int,
    val wordSize: Int,
    val argumentSize: Int,
    val arguments: Int,
    var ptr: Int,
    val memorySize: Int) {

    val programMemory: ByteArray = ByteArray(maxSize + 1)
    val memory: ByteArray = ByteArray(memorySize + 1)

    init {
        if (argumentSize * arguments != wordSize) {
            throw IllegalArgumentException("Arguments * size must be equal to word size")
        }
        println("A VM using id: ${this.identifier} is created")
    }

    fun write(value: Byte, ptr: Int = this.ptr) {
        if (ptr <= maxSize) {
            programMemory[ptr] = value
            this.ptr++
        } else {
            println("Memory overflow")
        }
    }

    fun write(value: ByteArray, ptr: Int = this.ptr) {
        if (ptr + value.size <= maxSize) {
            for (i in value.indices) {
                programMemory[ptr + i] = value[i]
            }
            this.ptr += value.size
        } else {
            println("Memory overflow")
        }
    }

    fun delete(ptr: Int = this.ptr) {
        if (ptr <= maxSize) {
            programMemory[ptr] = 0
        } else {
            println("Memory overflow")
        }
    }

    fun deleteAll() {
        for (i in 0 .. maxSize) {
            programMemory[i] = 0
        }
    }

    private fun connectBytes(bytes: ByteArray): Long {
        var result: Long = 0
        for (i in bytes.indices) {
            result = result shl 8 or ((bytes[i].toLong() and 0xFF))
        }
        return result
    }

    fun run() {
        for (runningPtr in 0 .. maxSize step wordSize) {
            println("Memory[$runningPtr, ${runningPtr + wordSize - 1}] =")
            for (i in 0 until arguments) {
                print("Argument ${i + 1}: ")
                val argumentBytes: ByteArray = ByteArray(argumentSize)

                for (j in 0 until argumentSize) {
                    argumentBytes[j] = programMemory[runningPtr + i * argumentSize + j]
                }

                print(connectBytes(argumentBytes))

                println(",")
            }
        }
    }

    fun save(filePath: String, headerData: ByteArray) {
        require(headerData.size == 16) { "Header data must be exactly 16 bytes" }

        val output: ByteArray = headerData + programMemory

        File(filePath).writeBytes(output)
        println("Memory saved to file: $filePath")
    }
}
