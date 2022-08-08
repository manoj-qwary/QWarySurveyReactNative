//
//  QWScheduleConfigurations.swift
//  QWarySurvey
//
//  Created by fahid on 13/07/2022.
//

import Foundation

@objc public class QWScheduleConfigurations: NSObject {
    
    // MARK: Properties
    @objc public static var testConfigs: QWScheduleConfigurations {
        let startDate = Date().addingTimeInterval(15)   //  mark survey start date
        let repeatInterval = TimeInterval(20)     // repeat survey after seconds
        return QWScheduleConfigurations(startDate: startDate, repeatSurvey: true, repeatInterval: repeatInterval)
    }
    var startDate: Date!
    var repeatInterval: TimeInterval!
    var repeatSurvey: Bool!

    // MARK: Initialize
    @objc public convenience init(startDate: Date,                    //  date with format dd/MM/yyyy HH:mm:ss
                repeatSurvey: Bool,                 //  should repeat survey
                repeatInterval: TimeInterval) {     //  time interval in seconds
        self.init()
        self.startDate = startDate
        self.repeatInterval = repeatInterval
        self.repeatSurvey = repeatSurvey
    }
}
