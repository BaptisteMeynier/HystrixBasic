package org.netflix.hystrix.customer.client;


import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;

/**
 * Matcher permettant de vérifier le tyde d'une RemoteCallException
 */
public class RemoteExceptionTypeMatcher extends TypeSafeMatcher<RemoteCallException> {

    final RemoteCallException.ExceptionType type;

    public RemoteExceptionTypeMatcher(RemoteCallException.ExceptionType type) {
        this.type = type;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expects type ").appendValue(type);
    }

    @Override
    protected void describeMismatchSafely(RemoteCallException exception, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(exception.getExceptionType());
    }

    @Override
    protected boolean matchesSafely(RemoteCallException e) {
        return e.getExceptionType() == type;
    }

}
