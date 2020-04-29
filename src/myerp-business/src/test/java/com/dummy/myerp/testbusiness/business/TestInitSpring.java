package com.dummy.myerp.testbusiness.business;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * Classe de test de l'initialisation du contexte Spring
 */
public class TestInitSpring extends BusinessTestCase {

    /**
     * Constructeur.
     */
    public TestInitSpring() {
        super();
    }


    /**
     * Teste l'initialisation du contexte Spring
     */
    @Test
    public void testInit() {
        com.dummy.myerp.testbusiness.business.SpringRegistry.init();
        assertNotNull(com.dummy.myerp.testbusiness.business.SpringRegistry.getBusinessProxy());
        assertNotNull(com.dummy.myerp.testbusiness.business.SpringRegistry.getTransactionManager());
    }
}
