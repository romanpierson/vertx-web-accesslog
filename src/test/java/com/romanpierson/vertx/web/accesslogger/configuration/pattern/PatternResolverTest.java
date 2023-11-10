package com.romanpierson.vertx.web.accesslogger.configuration.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.CookieElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.DateTimeElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.DurationElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.HeaderElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.MethodElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.RequestElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.StaticValueElement;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.StatusElement;

public class PatternResolverTest {

    @Test
    public void resolvePattern_off_the_shelf_pattern() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "server1 %{server1}static %{Accept-Encoding}i %t %D cs-uri %{foo}C";
        Collection<Class> expected = Arrays
                        .asList(StaticValueElement.class, HeaderElement.class, DateTimeElement.class, DurationElement.class,
                                        RequestElement.class, CookieElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_single_pattern_element() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%m";
        Collection<Class> expected = Collections.singletonList(MethodElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_two_pattern_elements() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%D %m";
        Collection<Class> expected = Arrays.asList(DurationElement.class, MethodElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_two_pattern_elements_reversed_order() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%m %D";
        Collection<Class> expected = Arrays.asList(MethodElement.class, DurationElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }

    @Test
    public void resolvePattern_duplicate_pattern_elements() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "%s %s %s";
        Collection<Class> expected = Arrays.asList(StatusElement.class, StatusElement.class, StatusElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
    }
    
    @Test
    public void resolvePattern_unresolvable_parts() {
        PatternResolver patternResolver = new PatternResolver();
        String logPattern = "xx %m yy";
        Collection<Class> expected = Arrays.asList(MethodElement.class);

        ResolvedPatternResult resolvedPatternResult = patternResolver.resolvePattern(logPattern);
        Collection<AccessLogElement> actual = resolvedPatternResult.getLogElements();

        assertResolvedPatterns(expected, actual);
        assertEquals("xx %s yy", resolvedPatternResult.getResolvedPattern());
    }

    private void assertResolvedPatterns(Collection<Class> expectedElementClasses, Collection<AccessLogElement> actual) {
        
    	assertEquals(expectedElementClasses.size(), actual.size(), actual.toString());
    	
        Iterator<AccessLogElement> actualIter = actual.iterator();
        Iterator<Class> expectedElementClassesIterator = expectedElementClasses.iterator();
        
        while (actualIter.hasNext()) {
            AccessLogElement actualElement = actualIter.next();
            Class expectedElementClass = expectedElementClassesIterator.next();
            assertTrue(actualElement.getClass().isAssignableFrom(expectedElementClass));
        }
    }
}