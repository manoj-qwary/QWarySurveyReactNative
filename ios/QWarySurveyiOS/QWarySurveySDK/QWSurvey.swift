//
//  QWSurvey.swift
//  QWarySurvey
//
//  Created by fahid on 01/07/2022.
//

import Foundation
import UIKit

@objc public class QWSurvey: NSObject, QWSurveyDelegate {

    // MARK: Properties
    private var request: QWSurveyRequest!
    private var surveyDelegate: QWSurveyDelegate!
    private var configurations: QWScheduleConfigurations!
    private var isAlreadyTakenKey = "isAlreadyTaken_"
    private var startDateKey = "startDate_"

    // MARK: Initialization
    @objc public convenience init(request: QWSurveyRequest, delegate: QWSurveyDelegate?, configurations: QWScheduleConfigurations) {
        self.init()
        self.request = request
        self.surveyDelegate = delegate
        self.configurations = configurations
        if let urlString = request.url?.absoluteString {
            isAlreadyTakenKey += urlString
            startDateKey += urlString
        }
    }

    // MARK: Public methods
    @objc public func scheduleSurvey(parent: UIViewController) {
        var promptDate: Date!
        if let nextPromptDateTime = UserDefaults.standard.value(forKey: startDateKey) as? Date {
            promptDate = nextPromptDateTime
            print("defaults survey next date/time: \(String(describing: promptDate))")
        }
        else {
            promptDate = configurations.startDate
            print("configs survey start date/time: \(String(describing: promptDate))")
        }
        guard let startDateTime = promptDate else {
            print("Error: Survey schedule start date time can not be nil")
            return
        }
        print("startDateTime: \(startDateTime)")
        if startDateTime < Date() {
            if !configurations.repeatSurvey {
                let isAlreadyTaken = UserDefaults.standard.bool(forKey: isAlreadyTakenKey)
                if isAlreadyTaken {
                    print("Survey is already taken and repeat is not allowed in configs.")
                    return
                }
            }
            let qwSurveyViewController = QWSurveyViewController(request: self.request, delegate: self)
            parent.present(qwSurveyViewController, animated: true, completion: nil)

            let nextPromptDateTime = Date().addingTimeInterval(configurations.repeatInterval)
            UserDefaults.standard.set(nextPromptDateTime, forKey: startDateKey)
            UserDefaults.standard.synchronize()
            
            print("Next survey start date/time \(nextPromptDateTime)")
            NotificationsManager.shared.scheduleNotification(request: request, configs: configurations, nextPromptDate: nextPromptDateTime)
        }
        else {
            UserDefaults.standard.set(startDateTime, forKey: startDateKey)
            UserDefaults.standard.synchronize()
        }
    }

    @objc public func clearSchedule() {
        UserDefaults.standard.removeObject(forKey: isAlreadyTakenKey)
        UserDefaults.standard.removeObject(forKey: startDateKey)
    }

    // MARK: Delegate
    public func didReceiveSurveyOutcome(_ state: QWSurveyState) {
        UserDefaults.standard.set(true, forKey: isAlreadyTakenKey)
        UserDefaults.standard.synchronize()
        surveyDelegate?.didReceiveSurveyOutcome(state)
    }
    
    public func didRedirectToURL(_ url: String) {
        surveyDelegate?.didRedirectToURL(url)
    }
}
