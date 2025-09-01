package com.moneymate.documentationManagement.core.utilities.exceptions.Results;

public class ErrorResult extends Result {

    public ErrorResult() {
        super(false);
    }

    public ErrorResult(String message) {
        super(false, message);
    }
}
