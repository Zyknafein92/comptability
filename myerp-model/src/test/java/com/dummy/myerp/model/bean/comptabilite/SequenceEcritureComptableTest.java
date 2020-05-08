package com.dummy.myerp.model.bean.comptabilite;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SequenceEcritureComptableTest {

    @Test
    public void testToString() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(300);

        String expected = "SequenceEcritureComptable{annee=" + sequenceEcritureComptable.getAnnee() + ", derniereValeur=" + sequenceEcritureComptable.getDerniereValeur() + "}";
        String result = sequenceEcritureComptable.toString();

        assertEquals(expected,result);
    }
}
