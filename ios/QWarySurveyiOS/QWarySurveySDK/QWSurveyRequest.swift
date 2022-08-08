//
//  QWSurveyRequest.swift
//  QWarySurvey
//
//  Created by fahid on 30/06/2022.
//

import Foundation

@objc public class QWSurveyRequest: NSObject {
    
    // MARK: Properties
    var scheme: String!
    var host: String!
    var path: String!
    var params: [String:String]!
    var url: URL? {
        var urlComponent = URLComponents()
        urlComponent.scheme = self.scheme
        urlComponent.host = self.host
        urlComponent.path = self.path
        urlComponent.queryItems = params.map {
            URLQueryItem(name: $0.key, value: $0.value)
        }
        return urlComponent.url
    }

    // MARK: Initialize
    @objc public convenience init(scheme: String, host: String, path: String, params: [String:String]) {
        self.init()
        self.scheme = scheme
        self.host = host
        self.path = path
        self.params = params
    }
}
