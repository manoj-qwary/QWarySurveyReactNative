package com.qwary.survey.model;

public class SurveyModel {

    String domain;
    String token;
    boolean loader;
    boolean modal;
    boolean isAlreadyTaken;
    long startAfter;
    long repeatInterval;

    public SurveyModel(String domain, String token, boolean loader, boolean modal, boolean isAlreadyTaken, long startAfter, long repeatInterval) {
        this.domain = domain;
        this.token = token;
        this.loader = loader;
        this.modal = modal;
        this.isAlreadyTaken = isAlreadyTaken;
        this.startAfter = startAfter;
        this.repeatInterval = repeatInterval;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLoader() {
        return loader;
    }

    public void setLoader(boolean loader) {
        this.loader = loader;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public boolean isAlreadyTaken() {
        return isAlreadyTaken;
    }

    public void setAlreadyTaken(boolean alreadyTaken) {
        isAlreadyTaken = alreadyTaken;
    }

    public long getStartAfter() {
        return startAfter;
    }

    public void setStartAfter(long startAfter) {
        this.startAfter = startAfter;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
