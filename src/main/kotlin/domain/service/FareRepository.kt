package com.example.domain.service

import com.example.domain.model.FareResult
import com.example.domain.model.Journey

interface FareRepository {
    fun saveFare(journey: Journey, fareResult: FareResult)
}