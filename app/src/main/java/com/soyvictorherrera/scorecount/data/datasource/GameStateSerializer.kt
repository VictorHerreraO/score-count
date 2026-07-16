package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.PlayerProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializer for GameStateProto used by Proto DataStore.
 * Handles reading and writing GameState to disk.
 */
object GameStateSerializer : Serializer<GameStateProto> {
    override val defaultValue: GameStateProto =
        GameStateProto
            .newBuilder()
            .setPlayer1(
                PlayerProto
                    .newBuilder()
                    .setId(1)
                    .setName("Player 1")
                    .setScore(0)
                    .build()
            ).setPlayer2(
                PlayerProto
                    .newBuilder()
                    .setId(2)
                    .setName("Player 2")
                    .setScore(0)
                    .build()
            ).setServingPlayerId(1)
            .setPlayer1SetsWon(0)
            .setPlayer2SetsWon(0)
            .setIsDeuce(false)
            .setIsFinished(false)
            .addAllChallengerQueue(emptyList())
            .build()

    override suspend fun readFrom(input: InputStream): GameStateProto {
        try {
            return GameStateProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: GameStateProto,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}
