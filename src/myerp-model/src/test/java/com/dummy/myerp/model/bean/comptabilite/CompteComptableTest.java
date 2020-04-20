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
    public void toStringTest(){
        this.compteComptable = new CompteComptable(listCompte.get(1).getNumero(),listCompte.get(1).getLibelle());
        String expectedString = "CompteComptable{numero=" + listCompte.get(1).getNumero() + ", libelle='" + listCompte.get(1).getLibelle() + "'}";
        String resultString = compteComptable.toString();
        assertThat(expectedString).isEqualTo(resultString);
    }

    @Test
    public void getByNumero_givenNull_ReturnNull() {
        CompteComptable expected = CompteComptable.getByNumero(listCompte, null);
        assertNull(expected);
    }

    @Test
    public void getByNumero_givenNumero_ReturnCompte() {
        CompteComptable expected = CompteComptable.getByNumero(listCompte, 2);
        assertThat(expected).isEqualTo(listCompte.get(1));
    }

    @Test
    public void getByNumero_givenWrongNumber_ReturnNull() {
        CompteComptable expected = CompteComptable.getByNumero(listCompte, 3);
        assertThat(expected).isNull();
    }
}
