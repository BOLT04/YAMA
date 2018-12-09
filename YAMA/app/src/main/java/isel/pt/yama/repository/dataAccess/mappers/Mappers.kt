package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository


class Mappers( repo: YAMARepository) {

    val userMapper = UserMapper(repo)
    val messageMapper = MessageMapper(repo)
    val organizationMapper = OrganizationMapper(repo)
    val teamMapper = TeamMapper(repo)
    val userAssociationMapper = UserAssociationMapper(repo)

}