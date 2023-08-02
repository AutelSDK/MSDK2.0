package com.autel.sdk.debugtools

/**
 * Scenario test data class for message and it's status
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 * @param message message about testing
 * @param status status about scenario test results
 */
class ScenarioTestResultDataModel(
    val message: String,
    val status: ScenarioTestResultStatusEnum = ScenarioTestResultStatusEnum.PROCESSING
)