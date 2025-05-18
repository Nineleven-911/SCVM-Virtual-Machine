package kt.hairinne.SCVM.VirtualMachine.old

import kt.hairinne.prepose.byteListInHex
import java.io.File


class StackVMOld(  // Initialize the virtual machine with the given parameters
    val identifier: Long,
    var ptr: Int,
    val memorySize: Int) {

    val programMemory: MutableList<List<Byte>> = mutableListOf()
    val memory: ByteArray = ByteArray(memorySize + 1)

    init {
        println("A VM using id: ${this.identifier} is created")
    }

    fun writeSingle(value: List<Byte>, ptr: Int = this.ptr) {
        programMemory[ptr] = value
        this.ptr++
    }

    fun writeMultiple(value: List<List<Byte>>, ptr: Int = this.ptr) {
        for (i in value.indices) {
            programMemory += value[i]
        }
        this.ptr += value.size
    }

    fun delete(ptr: Int = this.ptr) {
        programMemory.drop(ptr)
    }

    fun deleteAll() {
        programMemory.dropWhile { it.isEmpty() }
    }

    private fun connectBytes(bytes: List<Byte>): Int {
        var result: Int = 0
        for (i in bytes.indices) {
            result = result shl 8 or ((bytes[i].toInt() and 0xFF))
        }
        return result
    }

    // fun executes()

    fun run() {
        for (stat in programMemory.indices) {
            byteListInHex(programMemory[stat])
        }
    }

    fun save(filePath: String, headerData: ByteArray) {
        require(headerData.size == 16) { "Header data must be exactly 16 bytes" }
        val program: ByteArray = ByteArray( {
            var length: Int = 0
            for (i in programMemory.indices)
                length += programMemory[i].size
            length // Return
        }.invoke() )

        for (i in programMemory.indices) {
        }

        val output: ByteArray = headerData + program

        File(filePath).writeBytes(output)
        println("Memory saved to file: $filePath")
    }
}
