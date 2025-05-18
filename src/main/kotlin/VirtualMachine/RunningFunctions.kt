package kt.hairinne.SCVM.VirtualMachine

import kt.hairinne.SCVM.VirtualMachine.StackVM.StackVM


class RunningFunctions {
    companion object {
        fun errorReport(
            VM: StackVM,
            exception: Exception,
            save: Boolean = false) {
            println("SCVM exited.")

            println("""SCVM Arguments:
  Virtual Machine Settings:
    Type: "${VM.javaClass.name}";
    Identifier: ${VM.identifier};
    Pointer at: ${VM.ptr};
    Program: ${VM.program};
    Stack: ${VM.stack} (${VM.stack.size} Bytes);
    Memory: ${VM.memory};
    Version: ${VM.version};
""")

            println("""Exception Information: 
  Type: "${exception.javaClass.name}";
  Details: ${exception.message};
  Stack Trace: 
    ${exception.stackTraceToString()}""")
        }
    }
}
