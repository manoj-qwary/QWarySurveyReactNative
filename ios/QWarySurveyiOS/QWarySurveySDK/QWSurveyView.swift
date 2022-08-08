//
//  QWSurveyView.swift
//  QWarySurvey
//
//  Created by fahid on 30/06/2022.
//

import Foundation
import WebKit

@objc public class QWSurveyView: UIView, WKScriptMessageHandler, WKNavigationDelegate {

    // MARK: Properties
    private var qwWebView: WKWebView = WKWebView()
    private let surveyResponseHandler = WKUserContentController()
    private let loader: UIActivityIndicatorView = UIActivityIndicatorView()
    private var surveyDelegate: QWSurveyDelegate? = nil
    
    // MARK: Initialization
    @objc override init(frame: CGRect) {
        super.init(frame: frame)
        addFeedbackView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        addFeedbackView()
    }
    
    // MARK: Private methods
    private func addFeedbackView() {
        let config = WKWebViewConfiguration()
        config.userContentController = surveyResponseHandler
        qwWebView = WKWebView(frame: bounds, configuration: config)
        surveyResponseHandler.add(self, name: "observer")
        qwWebView.backgroundColor = .gray
        qwWebView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(qwWebView)
        qwWebView.addSubview(loader)
        qwWebView.navigationDelegate = self
        loader.translatesAutoresizingMaskIntoConstraints = false
        loader.centerXAnchor.constraint(equalTo: qwWebView.centerXAnchor).isActive = true
        loader.centerYAnchor.constraint(equalTo: qwWebView.centerYAnchor).isActive = true
        loader.hidesWhenStopped = true
    }

    private func redirect(_ redirectURL: String) {
        guard let url = URL(string: redirectURL) else {
            print("Error: Redirect url can not be nil")
            return
        }
        loader.startAnimating()
        let urlRequest = URLRequest(url: url)
        qwWebView.load(urlRequest)
    }

    // MARK: Delegate
    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        loader.stopAnimating()
    }
    
    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        loader.stopAnimating()
    }
    
    public func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        print(message.body)
        if let response = message.body as? [String: AnyObject] {
            if let outcome = response["OUTCOME"] as? String {
                let state = QWSurveyState(outcome: outcome)
                surveyDelegate?.didReceiveSurveyOutcome(state)
                if state == .redirect {
                    if let url = response["REDIRECT"] as? String {
                        surveyDelegate?.didRedirectToURL(url)
                        self.redirect(url)
                    }
                }
            }
        }
    }
    
    // MARK: Public method
    @objc public func loadSurvey(request: QWSurveyRequest, delegate: QWSurveyDelegate?) {
        guard let url = request.url else {
            print("Error: Request URL can not be nil")
            return
        }
        self.surveyDelegate = delegate
        loader.startAnimating()
        let urlRequest = URLRequest(url: url)
        qwWebView.load(urlRequest)
    }
}
