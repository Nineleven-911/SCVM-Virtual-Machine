package kt.hairinne.SCVM

import kt.hairinne.SCVM.VirtualMachine.RunningFunctions
import kt.hairinne.SCVM.VirtualMachine.StackVM.StackVM
import kt.hairinne.SCVM.prepose.KArgsParser
import kt.hairinne.prepose.byteListOfInts
import java.io.File
import kotlin.reflect.typeOf

fun main(args: Array<String>) {
    val stackVM = StackVM(
        identifier = 0x00000001,
        immediateSize = 64,
        ptr = 0
    )

    try {
        if (!args.isEmpty()) {

            when (args[0]) {
                "--compile" -> {
                    val file = File(args[1])
                    val fileContent = file.readText().lines()
                    stackVM.write(fileContent)
                    stackVM.save(args[2], byteListOfInts(
                        0, 0, 0xAE, 0xA0,
                        0, 0, 1, 0,
                        0, 0, 0, 0, 0, 0, 0, 0
                    ))
                }
                "--run" -> {
                    val file = File(args[1])
                    val fileContent = file.readBytes()

                    stackVM.writeByteCodes(
                        stackVM.analysisByteCodeCommands(fileContent, true)
                    )
                    stackVM.run(0)
                }
            }
        }
        /* stackVM.write(
            listOf<String>(
                "push 321",
                "push 123",
                "add",
                "print",
                "pop"
            )
        )

        stackVM.save("C:/Users/AW/Desktop/1.isc",
            byteListOfInts(
                0, 0, 0xAE, 0xA0,
                0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0
            )
        ) */

    } catch (e: Exception) {
        RunningFunctions.errorReport(stackVM, e)
    }
}
