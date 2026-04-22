package ufsm.petsi.petservices

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform