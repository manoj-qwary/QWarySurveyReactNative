//
//  QWSurveyViewController.swift
//  QWarySurvey
//
//  Created by fahid on 30/06/2022.
//

import Foundation
import UIKit

@objc public class QWSurveyViewController: UIViewController, QWSurveyDelegate {
    
    // MARK: Properties
    private var request: QWSurveyRequest!
    private var surveyDelegate: QWSurveyDelegate? = nil

    @objc public convenience init(request: QWSurveyRequest, delegate: QWSurveyDelegate?) {
        self.init()
        self.request = request
        self.surveyDelegate = delegate
    }
    
    // MARK: Initialize
    public override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = view.backgroundColor == nil ? .white : view.backgroundColor
        guard let request = self.request else {
            print("Error: Request can not be nil")
            return
        }
        let qwSurveyView = QWSurveyView()
        qwSurveyView.frame = view.bounds
        qwSurveyView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        qwSurveyView.loadSurvey(request: request, delegate: self)
        view.addSubview(qwSurveyView)
    }
    
    // MARK: Delegate
    public func didReceiveSurveyOutcome(_ state: QWSurveyState) {
        surveyDelegate?.didReceiveSurveyOutcome(state)
    }
    
    public func didRedirectToURL(_ url: String) {
        surveyDelegate?.didRedirectToURL(url)
    }
}
