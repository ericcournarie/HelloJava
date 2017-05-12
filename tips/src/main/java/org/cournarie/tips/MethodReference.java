/*
 * Copyright NellArmonia 2014
 */
package org.cournarie.tips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

/**
 * That was new in Java 8, method references.
 */
public class MethodReference {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(MethodReference.class);
    /** Countries. */
    private static final List<Country> COUNTRIES = new ArrayList<>();
    /** My countries. */
    private final List<String> myCountries = Arrays.asList("Italy", "Ausralia", "Canada", "France");

    /** Country creation. */
    static {
        COUNTRIES.add(new Country("France"));
        COUNTRIES.add(new Country("United states"));
        COUNTRIES.add(new Country("Brazil"));
        COUNTRIES.add(new Country("New zealand"));
        COUNTRIES.add(new Country("Japan"));
    }

    /**
     * Reference on a static method of a class.
     */
    public void referenceToStaticMethod() {
        LOGGER.info("referenceToStaticMethod");
        COUNTRIES.stream().
                map(MethodReference::toUpperCase).
                forEach(MethodReference::toPrinter);
    }

    /**
     * Reference on a constructor.
     */
    public void referenceToConstructor() {
        LOGGER.info("referenceToConstructor");
        List<Country> created = myCountries.stream().map(Country::new).collect(Collectors.toList());
        created.stream().map(MethodReference::toUpperCase).forEach(MethodReference::toPrinter);
    }

    /**
     * Reference to an instance method of an arbitrary object of a particular type.
     */
    public void referenceToInstanceMethod() {
        LOGGER.info("referenceToInstanceMethod");
        COUNTRIES.stream().
                map(Country::getName).
                forEach(MethodReference::toPrinter);
    }

    /**
     * Reference to an instance method of a particular object.
     */
    public void referenceToInstanceMethodOfObject() {
        LOGGER.info("referenceToInstanceMethodOfObject");
        COUNTRIES.stream().filter(this::belongsToMe).
                map(MethodReference::toUpperCase).
                forEach(MethodReference::toPrinter);
    }

    /**
     * An instance method of a particular object.
     * @param c
     * @return
     */
    public boolean belongsToMe(Country c) {
        return myCountries.contains(c.getName());
    }

    /**
     * Static method.
     * @param c
     * @return
     */
    public static String toUpperCase(Country c) {
        return c.getName().toUpperCase();

    }

    /**
     * Static method, to get some informations on what happens...
     * @param name
     */
    public static void toPrinter(String name) {
        LOGGER.info("Country name: " + name);
    }

    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {

        MethodReference ref = new MethodReference();

        ref.referenceToStaticMethod();
        ref.referenceToConstructor();
        ref.referenceToInstanceMethod();
        ref.referenceToInstanceMethodOfObject();
    }

    /**
     * A country.
     */
    public static class Country {

        /** The name of this country. */
        private final String name;

        public Country(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
