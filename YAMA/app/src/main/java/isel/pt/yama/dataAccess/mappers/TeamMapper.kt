package isel.pt.yama.dataAccess.mappers

import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.model.TeamMD

public class TeamMapper(repo : YAMARepository){

    fun dtoToMD(team : TeamDto): TeamMD=
            TeamMD(
                    team.name,
                    team.id,
                    team.description
            )

    fun dbToMD(team: Team): TeamMD =
            TeamMD(team.name, team.id, team.description)
}