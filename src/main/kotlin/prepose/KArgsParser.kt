package kt.hairinne.SCVM.prepose

class KArgsParser(private val progName: String = "program") {
    private val arguments = mutableMapOf<String, Argument>()
    private var description: String = ""

    fun setDescription(description: String) {
        this.description = description
    }

    fun addArgument(
        flag: String,
        help: String = "",
        required: Boolean = false,
        defaultValue: Any? = null,
        isFlag: Boolean = false
    ) {
        arguments[flag] = Argument(flag, help, required, defaultValue, isFlag)
    }

    fun parseArgs(args: Array<String>): Map<String, Any?> {
        val parsed = mutableMapOf<String, Any?>()

        // 初始化默认值
        for (arg in arguments.values) {
            parsed[arg.flag] = arg.defaultValue
        }

        var i = 0
        while (i < args.size) {
            val arg = args[i]
            if (!arguments.containsKey(arg)) {
                throw IllegalArgumentException("Unknown argument: $arg")
            }

            val argument = arguments[arg]!!
            if (argument.isFlag) {
                parsed[arg] = true
                i++
            } else {
                if (i + 1 >= args.size) {
                    throw IllegalArgumentException("Missing value for argument: $arg")
                }
                parsed[arg] = args[++i]
                i++
            }
        }

        // 检查必填参数
        for (arg in arguments.values) {
            if (arg.required && parsed[arg.flag] == null) {
                throw IllegalArgumentException("Missing required argument: ${arg.flag}")
            }
        }

        return parsed
    }

    fun printHelp() {
        println("$progName: ${description.ifEmpty { "Command line utility" }}")
        println("Usage:")
        for (arg in arguments.values) {
            val req = if (arg.required) "required" else "optional"
            val def = if (arg.defaultValue != null) "default: ${arg.defaultValue}" else ""
            println("  $${arg.flag} [value]  $req  $def  ${arg.help}")
        }
    }

    private data class Argument(
        val flag: String,
        val help: String,
        val required: Boolean,
        val defaultValue: Any?,
        val isFlag: Boolean
    )
}
