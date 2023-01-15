package playground.patterns

interface Prop {
    val name: String
    val value: Any
}

interface ServerConfig {
    val properties: List<Prop>
}

data class PropImpl(
    override val name: String,
    override val value: Any
) : Prop

data class ServerConfigImpl(
    override val properties: List<Prop>
) : ServerConfig


fun prop(input: String): Prop {
    val (name, value) = input.split(":")
    return when (name) {
        "port" -> PropImpl(name, value.trim().toInt())
        "environment" -> PropImpl(name, value.trim())
        else -> throw RuntimeException("")
    }
}


