package isel.pt.yama.dataAccess.mappers

import com.android.volley.VolleyError
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dto.UserAssociationDto
import isel.pt.yama.model.UserAssociation

class UserAssociationMapper(val repo: YAMARepository) {

    fun dtoToMD(userAssociationDto : UserAssociationDto, cb : (UserAssociation) -> Unit){
        repo.getUser(
                userAssociationDto.userLogin,
                { cb(UserAssociation(userAssociationDto.chatId, it))},
                { error -> throw Error(error) }
                )
    }


}
