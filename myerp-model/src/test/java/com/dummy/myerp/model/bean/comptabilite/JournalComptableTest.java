package com.dummy.myerp.model.bean.comptabilite;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class JournalComptableTest {

    JournalComptable journalComptable;

    List<JournalComptable> test = new ArrayList<>();

    @Before
    public void setUp() {
        journalComptable = new JournalComptable();
        test.add(new JournalComptable("test1","test1"));
        test.add(new JournalComptable("test2","test2"));
    }

    @Test
    public void testToString() {
        journalComptable.setLibelle("Test");
        journalComptable.setCode("25");

        String expected = "JournalComptable{code='" + journalComptable.getCode() +"', libelle='" + journalComptable.getLibelle() + "'}";
        String result = journalComptable.toString();

        assertEquals(expected,result);
    }

    @Test
    public void getByCode_GivenNullCode_ReturnNull() {
        JournalComptable expected = JournalComptable.getByCode(test,null);
        assertNull(expected);
    }

    @Test
    public void getByCode_GivenCode_ReturnCode() {
        JournalComptable expected = JournalComptable.getByCode(test,"test1");
        assertThat(expected).isEqualTo(test.get(0));
    }

    @Test
    public void getByCode_GivenWrongCode_ReturnNull(){
        JournalComptable expected = JournalComptable.getByCode(test,"test3");
        assertThat(expected).isNull();
    }

}
