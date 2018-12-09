package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.dataAccess.database.UserDB
import isel.pt.yama.repository.dataAccess.dto.UserDto
import isel.pt.yama.repository.model.User

class UserMapper(val repo: YAMARepository){

    fun dbToModel(userDB : UserDB): User =
            User(
                    login = userDB.login,
                    id = userDB.id,
                    avatar_url = userDB.avatarUrl,
                    name = userDB.name,
                    email = userDB.email,
                    followers = userDB.followers,
                    following = userDB.following
            )

    fun dtoToModel(user : UserDto): User =
            User(
                    login = user.login,
                    id = user.id,
                    avatar_url = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )

    fun modelToDto(user : User) : UserDto =
            UserDto(
                    login = user.login,
                    id = user.id,
                    avatar_url = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )

    fun dtoToDb(user : UserDto): UserDB =
            UserDB(
                    login = user.login,
                    id = user.id,
                    avatarUrl = user.avatar_url,
                    name = user.name,
                    email = user.email,
                    followers = user.followers,
                    following = user.following
            )
}