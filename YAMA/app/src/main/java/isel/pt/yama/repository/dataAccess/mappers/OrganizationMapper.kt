package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.dataAccess.database.OrganizationDB
import isel.pt.yama.repository.dataAccess.dto.OrganizationDto
import isel.pt.yama.repository.model.Organization


public class OrganizationMapper(val repo : YAMARepository){

    fun dbToModel(org : OrganizationDB): Organization =
            Organization(
                    login = org.login,
                    id = org.id
            )

    fun dtoToModel(org : OrganizationDto): Organization =
            Organization(
                    login = org.login,
                    id = org.id
            )

}