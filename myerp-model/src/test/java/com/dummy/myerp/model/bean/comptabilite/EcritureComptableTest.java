package com.dummy.myerp.model.bean.comptabilite;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class EcritureComptableTest {

    private EcritureComptable vEcriture;

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
        return vRetour;
    }

    @Before
    public void setUp() {
        vEcriture = new EcritureComptable();
    }

    @Test
    public void isEquilibree() {
        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.06", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.06", "33.12"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "300"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

    /**
     Test getTotalDebit()
     **/

    @Test
    public void testGetTotalDebit_GivenEmptyOrNoIncome_DebitReturnZero () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null,null));
        assertEquals(BigDecimal.valueOf(0), vEcriture.getTotalDebit());
    }

    @Test
    public void testGetTotalDebit_GivenOneDebit_ReturnDebit () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,"10.04",null));
        assertEquals(BigDecimal.valueOf(10.04), vEcriture.getTotalDebit());
    }

    @Test
    public void testGetTotalDebit_GivenMultipleDebit_ReturnDebit () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,"10",null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1,"100",null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1,"200",null));
        assertEquals(BigDecimal.valueOf(310), vEcriture.getTotalDebit());
    }

    /**
     Test  getTotalCredit()
     **/

    @Test
    public void testGetTotalCredit_GivenEmptyOrNoIncomeCredit_ReturnZero () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null,null));
        assertEquals(BigDecimal.valueOf(0), vEcriture.getTotalCredit());

        vEcriture.getListLigneEcriture().add(this.createLigne(1,"0",null));
        assertEquals(BigDecimal.valueOf(0), vEcriture.getTotalCredit());
    }

    @Test
    public void testGetTotalCredit_GivenOneCredit_ReturnCrebit () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null ,"20"));
        assertEquals(BigDecimal.valueOf(20), vEcriture.getTotalCredit());
    }

    @Test
    public void testGetTotalCredit_GivenMultipleCredit_ReturnCrebit () {
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null,"30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null,"100.11"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1,null,"70"));
        assertEquals(BigDecimal.valueOf(200.11), vEcriture.getTotalCredit());
    }

    @Test
    public void toStringTest(){
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("test");
        ecritureComptable.setJournal(new JournalComptable("testCode", "testLibelle"));
        ecritureComptable.setId(1);
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AA-0000-00000");

        String expectedString = "EcritureComptable{id=" + ecritureComptable.getId() +
                ", journal="+ecritureComptable.getJournal() +
                ", reference='"+ecritureComptable.getReference()+"'" +
                ", date=" + ecritureComptable.getDate() +
                ", libelle='" + ecritureComptable.getLibelle() + "'" +
                ", totalDebit=" + ecritureComptable.getTotalDebit().toPlainString() +
                ", totalCredit=" + ecritureComptable.getTotalCredit().toPlainString() +
                ", listLigneEcriture=[\n" +
                StringUtils.join(ecritureComptable.getListLigneEcriture(), "\n")+
                "\n]}";
        String resultString = ecritureComptable.toString();

        Assert.assertEquals(expectedString, resultString);
    }
}

