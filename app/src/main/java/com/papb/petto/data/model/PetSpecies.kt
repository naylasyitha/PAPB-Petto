package com.papb.petto.data.model

enum class PetSpecies(
    val dbValue: String,
    val label: String,
    val chipLabel: String
) {
    DOG("dog", "Dog", "Dogs"),
    CAT("cat", "Cat", "Cats"),
    BIRD("bird", "Bird", "Birds"),
    RABBIT("rabbit", "Rabbit", "Rabbits"),
    REPTILE("reptile", "Reptile", "Reptiles"),
    OTHER("other", "Other", "Other");

    companion object {
        fun fromDb(value: String): PetSpecies =
            entries.firstOrNull { it.dbValue == value } ?: OTHER
    }
}
