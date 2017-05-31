package org.hibernate.tool.test.support;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ResultCaptor<T> implements Answer {
    private T result;

    public T getResult() {
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T answer(InvocationOnMock invocationOnMock) throws Throwable {
        result = (T) invocationOnMock.callRealMethod();
        return result;
    }
}