package isel.pt.yama.dataAccess.mappers

import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.UserDto
import isel.pt.yama.model.UserMD

class UserMapper(val repo: YAMARepository){

    fun dbToMD(user : User): UserMD =
            UserMD(
                    login = user.login,
                    id = user.id,
                    avatar_url = user.avatarUrl,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )

    fun dtoToMD(user : UserDto): UserMD =
            UserMD(
                    login = user.login,
                    id = user.id,
                    avatar_url = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )

    fun mdToDto(user : UserMD) : UserDto =
            UserDto(
                    login = user.login,
                    id = user.id,
                    avatar_url = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )

    fun dtoToDb(user : UserDto): User =
            User(
                    login = user.login,
                    id = user.id,
                    avatarUrl = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )
}