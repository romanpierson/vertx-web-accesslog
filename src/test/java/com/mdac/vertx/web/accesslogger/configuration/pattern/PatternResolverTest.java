package com.mdac.vertx.web.accesslogger.configuration.pattern;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.CookieElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.DateTimeElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.DurationElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.HeaderElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.MethodElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.RequestElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.StaticValueElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.StatusElement;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PatternResolverTest {

    @Test
    public void resolvePattern_off_the_shelf_pattern() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "server1 %{server1}static %{Accept-Encoding}i %t %D cs-uri %{foo}C";
        Collection<AccessLogElement> expected = Arrays
                        .asList(new StaticValueElement(), new HeaderElement(), new DateTimeElement(), new DurationElement(),
                                        new RequestElement(), new CookieElement());

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_single_pattern_element() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%m";
        Collection<AccessLogElement> expected = Collections.singletonList(new MethodElement());

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_two_pattern_elements() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%D %m";
        Collection<AccessLogElement> expected = Arrays.asList(new DurationElement(), new MethodElement());

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_two_pattern_elements_reversed_order() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%m %D";
        Collection<AccessLogElement> expected = Arrays.asList(new MethodElement(), new DurationElement());

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_duplicate_pattern_elements() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%s %s %s";
        Collection<AccessLogElement> expected = Arrays.asList(new StatusElement(), new StatusElement(), new StatusElement());

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    private void assertResolvedPatterns(Collection<AccessLogElement> expected, Collection<AccessLogElement> actual) {
        assertEquals(actual.toString(), expected.size(), actual.size());
        Iterator<AccessLogElement> actualIter = actual.iterator();
        Iterator<AccessLogElement> expectedIter = expected.iterator();
        while (actualIter.hasNext()) {
            AccessLogElement actualElement = actualIter.next();
            AccessLogElement expectedElement = expectedIter.next();
            assertTrue(actualElement.getClass().isAssignableFrom(expectedElement.getClass()));
        }
    }
}