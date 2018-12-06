package isel.pt.yama.dataAccess.mappers

import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD
import isel.pt.yama.model.SentMessageMD

public class MessageMapper(val repo : YAMARepository){

    fun mdToDto(messageMD: MessageMD): MessageDto =
            MessageDto(messageMD.user.login,
                    messageMD.content,
                    messageMD.createdAt)



    fun dtoToMD(dto: MessageDto, cb: (MessageMD) -> Unit) =
            repo.getUser(dto.user,
                    {
                        if (it == repo.currentUser)
                            cb(SentMessageMD(it, dto.content, dto.createdAt))
                        else
                            cb(ReceivedMessageMD(it, dto.content, dto.createdAt))

                    },
                    { error -> throw Error(error) }//TODO shady biz
            )

}