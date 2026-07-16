package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import com.soyvictorherrera.scorecount.domain.model.Player
import javax.inject.Inject

class PlayerProfileMapper
    @Inject
    constructor() {
        fun mapFromEntity(entity: PlayerProfileEntity): Player =
            Player(
                id = entity.id.toInt(),
                name = entity.name,
                score = 0,
                color = null
            )

        fun mapToEntity(domain: Player): PlayerProfileEntity =
            PlayerProfileEntity(
                id = domain.id.toLong(),
                name = domain.name
            )
    }
