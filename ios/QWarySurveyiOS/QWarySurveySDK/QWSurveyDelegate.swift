//
//  QWSurveyDelegate.swift
//  QWarySurvey
//
//  Created by fahid on 30/06/2022.
//

import Foundation

@objc public enum QWSurveyState: Int {
    case unknown = 0, completed, disqualified, terminated, redirect
    init(outcome: String) {
        switch outcome {
        case "SURVEY_COMPLETED":
            self = .completed
        case "SURVEY_DISQUALIFIED":
            self = .disqualified
        case "SURVEY_TERMINATED":
            self = .terminated
        case "QWARY_REDIRECT":
            self = .redirect
        default:
            self = .unknown
        }
    }
}

@objc public protocol QWSurveyDelegate {
    func didReceiveSurveyOutcome(_ state: QWSurveyState)
    func didRedirectToURL(_ url: String)
}
