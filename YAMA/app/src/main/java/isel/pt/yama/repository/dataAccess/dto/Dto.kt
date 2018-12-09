package isel.pt.yama.repository.dataAccess.dto

interface Dto<T> {

    fun fromObj(obj : T): Dto<T>

    fun toObj(dto : Dto<T>) : T
}