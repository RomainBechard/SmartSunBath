package lucile.m1.smartsunbath

enum class BLEUUIDMatching(val uuid: String, val title: String) {
    GENERIC_ACCESS("1800", "Accès Générique"),
    GENERIC_ATTRIBUTE("1801", "Attribut Générique"),
    DEVICE_NAME("2A00", "Nom du périphérique"),
    UNKNOWN_SERVICE("0000fe40-cc7a-482a-984a-7f2ed5b3e58f", "Service Inconnu"),
    UNKNOWN_CHARACTERISTIC_1("0000fe41-8e22-4541-9d4c-21edae82ed19", "Charactéristic inconnue 1"),
    UNKNOWN_CHARACTERISTIC_2("0000fe42-8e22-4541-9d4c-21edae82ed19", "Charactéristic inconnue 2");

    companion object {
        fun getBLEAttributeFromUUID(uuid: String) =
            values().firstOrNull { it.uuid == uuid } ?: UNKNOWN_SERVICE
    }
}