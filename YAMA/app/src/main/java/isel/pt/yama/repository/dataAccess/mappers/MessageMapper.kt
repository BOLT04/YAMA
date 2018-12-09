package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.dataAccess.dto.MessageDto
import isel.pt.yama.repository.model.Message
import isel.pt.yama.repository.model.ReceivedMessage
import isel.pt.yama.repository.model.SentMessage

public class MessageMapper(val repo : YAMARepository){

    fun modelToDto(message: Message): MessageDto =
            MessageDto(message.user.login,
                    message.content,
                    message.createdAt)



    fun dtoToModel(dto: MessageDto, cb: (Message) -> Unit) =
            repo.getUser(dto.user,
                    {
                        if (it == repo.currentUser)
                            cb(SentMessage(it, dto.content, dto.createdAt))
                        else
                            cb(ReceivedMessage(it, dto.content, dto.createdAt))

                    },
                    { error -> throw Error(error) }
            )

}