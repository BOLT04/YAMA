package isel.pt.yama.repository.dataAccess.mappers

import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.dataAccess.database.TeamDB
import isel.pt.yama.repository.dataAccess.dto.TeamDto
import isel.pt.yama.repository.model.Team

public class TeamMapper(repo : YAMARepository){

    fun dtoToModel(team : TeamDto): Team=
            Team(
                    team.name,
                    team.id,
                    team.description
            )

    fun dbToModel(teamDB: TeamDB): Team =
            Team(teamDB.name, teamDB.id, teamDB.description)
}