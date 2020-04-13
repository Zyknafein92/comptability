package com.dummy.myerp.model.bean.comptabilite;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class CompteComptableTest {

    CompteComptable compteComptable;
    List<CompteComptable> listCompte = new ArrayList<>();

    @Before
    public void setUp() {
        compteComptable = new CompteComptable();
        listCompte.add(new CompteComptable(1, "test1"));
        listCompte.add(new CompteComptable(2, "test2"));
    }

    @Test
    public void testToString() {
        compteComptable.setLibelle("test");
        compteComptable.setNumero(10);

        String expected = "CompteComptable{numero=" + compteComptable.getNumero() + ", libelle='" + compteComptable.getLibelle() + "'}";
        String result = compteComptable.toString();

        assertEquals(expected,result);
    }

    @Test
    public void givenNull_ReturnNull() {
        CompteComptable expected = CompteComptable.getByNumero(listCompte, null);
        assertNull(expected);
    }

    @Test
    public void givenNumero_ReturnCompte() {
        CompteComptable expected = CompteComptable.getByNumero(listCompte, 2);
        assertThat(expected).isEqualTo(listCompte.get(1));
    }
}
