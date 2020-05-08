package com.dummy.myerp.model.bean.comptabilite;



import org.junit.Test;


import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LigneEcritureComptableTest {

    @Test
    public void testToString() {
        LigneEcritureComptable ligne = new LigneEcritureComptable(
                new CompteComptable(1,"test"),
                "testLigne",
                new BigDecimal(300.00),
                new BigDecimal(200));

        String expected= "LigneEcritureComptable{compteComptable="+ligne.getCompteComptable()+
                ", libelle='"+ligne.getLibelle()+"', "+
                "debit="+ligne.getDebit()+", "+
                "credit="+ligne.getCredit()+"}";

        String resultString = ligne.toString();

        assertEquals(expected, resultString);
    }
}
