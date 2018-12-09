package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.dataAccess.dto.UserAssociationDto
import isel.pt.yama.repository.model.UserAssociation

class UserAssociationMapper(val repo: YAMARepository) {

    fun dtoToModel(userAssociationDto : UserAssociationDto, cb : (UserAssociation) -> Unit){
        repo.getUser(
                userAssociationDto.userLogin,
                { cb(UserAssociation(userAssociationDto.chatId, it))},
                { error -> throw Error(error) }
                )
    }


}
