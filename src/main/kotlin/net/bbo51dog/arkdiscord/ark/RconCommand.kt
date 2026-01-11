package net.bbo51dog.arkdiscord.ark

enum class RconCommand(
    val label: String,
) {

    LIST_PLAYERS("ListPlayers"),
    GET_MAP("GetMap"),
    GET_VERSION("GetVersion"),
    GET_DAY("GetDay"),
}