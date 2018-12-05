package isel.pt.yama.dataAccess.mappers

import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.Organization
import isel.pt.yama.dto.OrganizationDto
import isel.pt.yama.model.OrganizationMD


public class OrganizationMapper(val repo : YAMARepository){

    fun dbToMD(org : Organization): OrganizationMD =
            OrganizationMD(
                    login = org.login,
                    id = org.id
            )

    fun dtoToMD(org : OrganizationDto): OrganizationMD =
            OrganizationMD(
                    login = org.login,
                    id = org.id
            )

}