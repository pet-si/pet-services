package ufsm.petsi.petservices.util

import io.ktor.util.hex
import java.security.MessageDigest


// [W.I.P] ainda quero adicionar uma biblioteca pra deixar mais seguro com salting
@OptIn(ExperimentalStdlibApi::class)
fun hash(text : String) : String {
    val md = MessageDigest.getInstance("SHA-256")
    val combinedBytes = text.toByteArray(Charsets.UTF_8)
    val digest = md.digest(combinedBytes)
    return digest.toHexString()
}