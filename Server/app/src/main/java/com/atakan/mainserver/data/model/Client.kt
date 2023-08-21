package com.atakan.mainserver.data.model

data class Client(
    var clientPackageName: String? = null,
    var clientProcessId: String? = null,
    var clientCurr1: String? = null,
    var clientCurr2: String? = null,
    var clientCurr3: String? = null,
    var clientRate1: Double? = null,
    var clientRate2: Double? = null,
    var clientRate3: Double? = null,
    var time : String? = null,
    var ipcMethod: String = ""
)

object RecentClient {
    var client: Client? = null
}