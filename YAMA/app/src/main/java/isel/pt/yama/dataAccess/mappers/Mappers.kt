package isel.pt.yama.dataAccess.mappers

import isel.pt.yama.YAMAApplication
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.dto.UserDto
import isel.pt.yama.model.*




class Mappers( repo: YAMARepository) {

    val userMapper = UserMapper(repo)
    val messageMapper = MessageMapper(repo)
    val organizationMapper = OrganizationMapper(repo)
    val teamMapper = TeamMapper(repo)

}