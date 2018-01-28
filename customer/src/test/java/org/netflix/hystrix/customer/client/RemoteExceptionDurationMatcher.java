package org.netflix.hystrix.customer.client;


import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;

/**
 * Matcher permettant de vérifier la durée de l'appel qui a généré l'exception
 */
public class RemoteExceptionDurationMatcher extends TypeSafeMatcher<RemoteCallException> {

    final int callDuration;

    public RemoteExceptionDurationMatcher(int callDuration) {
        this.callDuration = callDuration;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expects callDuration ").appendValue(callDuration);

    }

    @Override
    protected void describeMismatchSafely(RemoteCallException exception, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(exception.getCallDuration());
    }

    @Override
    protected boolean matchesSafely(RemoteCallException e) {
        return e.getCallDuration() == callDuration;
    }

}
