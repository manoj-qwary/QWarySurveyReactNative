//
//  NotificationsManager.swift
//  QWarySurvey
//
//  Created by fahid on 03/08/2022.
//

import Foundation
import UserNotifications
import UIKit

@objc public protocol NotificationsScheduleType {
    func scheduleNotification(request: QWSurveyRequest, configs: QWScheduleConfigurations, nextPromptDate: Date)
}

@objc public class NotificationsManager: NSObject,
                                   NotificationsScheduleType,
                                   UNUserNotificationCenterDelegate {
    @objc public static let shared = NotificationsManager()
    @objc private override init() {}
    
    @objc public func scheduleNotification(request: QWSurveyRequest, configs: QWScheduleConfigurations, nextPromptDate: Date) {
        requestNotificationsPermission { [weak self] in
            guard let self = self else { return }
            let content = UNMutableNotificationContent()
            content.title = "QWSurvey Alert!"
            content.subtitle = ""
            content.body = "Please open the app to give feedback for your scheduled survey."
            let timeInterval = abs(nextPromptDate.timeIntervalSince(Date()))
            print("Local Notification Time Left: \(timeInterval) seconds")
            
            let trigger = UNTimeIntervalNotificationTrigger(timeInterval: timeInterval,
                                                            repeats: false)
            
            let urlString = request.url?.absoluteString ?? ""
            let requestIdentifier = urlString
            let request = UNNotificationRequest(identifier: requestIdentifier,
                                                content: content, trigger: trigger)
            UNUserNotificationCenter.current().add(request,
                                                   withCompletionHandler: { [weak self] (error) in
                guard let _ = self else { return }
                if let error = error {
                    print("Error: \(error.localizedDescription)")
                }
            })
        }
    }
    
    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .sound])
    }
    
    private func requestNotificationsPermission(_ completion: @escaping ()->Void) {
       
        UNUserNotificationCenter.current().requestAuthorization(options:
                                                                    [[.alert, .sound, .badge]],
                                                                completionHandler: { [weak self] (granted, error) in
            guard let _ = self else { return }
            if let error = error {
                print("Error: \(error.localizedDescription)")
                return
            }
            if !granted {
                print("Error: Notifications permissions is not granted")
                return
            }
            completion()
        })
        UNUserNotificationCenter.current().delegate = self
    }
}
