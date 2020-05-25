package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();


    EcritureComptable vEcritureComptable;

    @Before
    public void setup() {
        manager = new ComptabiliteManagerImpl();
        vEcritureComptable = new EcritureComptable();
        JournalComptable pJournal = new JournalComptable("AC", "Achat");
        vEcritureComptable.setJournal(pJournal);
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");

    }


     /*
                                          checkEcritureComptableUnit
    */

    @Test
    public void testcheckEcritureComptableUnit_GivenInvalidForm_ReturnFunctionnalError() {
        EcritureComptable vEcritureComptable = new EcritureComptable();

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les règles de gestion.");
    }

    @Test
    public void testCheckEcritureComptableUnit_GivenInvalidLibelle_ReturnFunctionnalError() {
        vEcritureComptable.setLibelle("");

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les règles de gestion.");
    }

    @Test
    public void testCheckEcritureComptableUnitRG2_GivenNotBalanceEcritureComptable_ReturnFunctionnalError() {

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(223), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null, new BigDecimal(123)));


        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable n'est pas équilibrée.");
    }

    @Test
    public void testCheckEcritureComptableUnitRG3_GivenTwoCreditsNoDebit_ReturnFunctionnalError() {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,  null,
                new BigDecimal(120)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,  null,
                new BigDecimal(120)));

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo( "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
    }

    @Test
    public void testCheckEcritureComptableUnitRG5_GivenBadJournalCode_ReturnFunctionnalError() {
        vEcritureComptable.setReference("BQ-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(453), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(453)));

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo( "Le code journal " + vEcritureComptable.getJournal().getCode() + " ne correspond à celui de la référence "
                        + "BQ");
    }

    @Test
    public void testCheckEcritureComptableUnitRG5_GivenBadYear_ReturnFunctionnalError() {
        vEcritureComptable.setReference("AC-2022/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(453), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(453)));

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'année de référence ne correspond pas à l'année courante");
    }

    @Test
    public void testCheckEcritureComptableUnitRG5_GivenNullReferenceValue_ReturnFunctionnalError() {
        vEcritureComptable.setReference(null);
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(453), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(453)));

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("La référence de l'écriture comptable ne peut pas être nulle");
    }


    @Test
    public void testStringFormatRG5() {
        int num = 1111;
        String formatted = String.format("%05d", num);
        assertThat(formatted).isEqualTo("01111");

        int number = 4;
        String formatted2 = String.format("%05d", number);
        assertThat(formatted2).isEqualTo("00004");

        int number2 = 110;
        String formatted3 = String.format("%05d", number2);
        assertThat(formatted3).isEqualTo("00110");

        int number3 = 52345;
        String formatted4 = String.format("%05d", number3);
        assertThat(formatted4).isEqualTo("52345");
    }


//    @Test
//    public void checkEcritureComptableUnit() throws Exception {
//        EcritureComptable vEcritureComptable;
//        vEcritureComptable = new EcritureComptable();
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(new Date());
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.setReference("AC-2020/00001");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                                                                                 null, new BigDecimal(123),
//                                                                                 null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
//                                                                                 null, null,
//                                                                                 new BigDecimal(123)));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
//    }
//
//    @Test(expected = FunctionalException.class)
//    public void checkEcritureComptableUnitViolation() throws Exception {
//        EcritureComptable vEcritureComptable;
//        vEcritureComptable = new EcritureComptable();
//        manager.checkEcritureComptableUnit(vEcritureComptable);
//    }
//
//    @Test(expected = FunctionalException.class)
//    public void checkEcritureComptableUnitRG2() throws Exception {
//        EcritureComptable vEcritureComptable;
//        vEcritureComptable = new EcritureComptable();
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(new Date());
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                                                                                 null, new BigDecimal(123),
//                                                                                 null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
//                                                                                 null, null,
//                                                                                 new BigDecimal(123)));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
//    }
//
//    @Test(expected = FunctionalException.class)
//    public void checkEcritureComptableUnitRG3() throws Exception {
//        EcritureComptable vEcritureComptable;
//        vEcritureComptable = new EcritureComptable();
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(new Date());
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                                                                                 null, new BigDecimal(123),
//                                                                                 null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                                                                                 null, new BigDecimal(123),
//                                                                                 null));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
//    }

}
