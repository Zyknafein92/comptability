package com.dummy.myerp.business.impl;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComtabilititeManagangerImplIntTest {
    @Mock
    DaoProxy dao;

    @Mock
    ComptabiliteDao comptabiliteDao;

    @Mock
    BusinessProxy businessProxy;

    @Mock
    TransactionManager transactionManager;


    ComptabiliteManagerImpl manager;

    EcritureComptable vEcritureComptable;

    @Before
    public void setup() {
        manager = new ComptabiliteManagerImpl();
        when(dao.getComptabiliteDao()).thenReturn(comptabiliteDao);
        ComptabiliteManagerImpl.configure(businessProxy, dao, transactionManager);

        vEcritureComptable = new EcritureComptable();
        JournalComptable pJournal = new JournalComptable("AC", "Achat");
        vEcritureComptable.setJournal(pJournal);
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
    }

   /*
                                 AddReference
    */

    @Test
    public void testAddReferenceNotExistReturnNew() throws Exception {
        when(comptabiliteDao.getSequenceEcritureComptable(vEcritureComptable.getJournal().getCode(), vEcritureComptable.getDate().getYear()))
                .thenReturn(null);

        manager.addReference(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable(any());
    }


    @Test
    public void testAddReferenceExistReturnUpdateValue() throws Exception {
        JournalComptable pJournal = new JournalComptable("AC", "Achat");

        SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable(pJournal,2020, 00001);

        when(comptabiliteDao.getSequenceEcritureComptable(vEcritureComptable.getJournal().getCode(), vEcritureComptable.getDate().getYear()))
                .thenReturn(vSequenceEcritureComptable);

        manager.addReference(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(comptabiliteDao, times(1)).updateSequenceEcritureComptable(any());
    }

    /*
                                          checkEcritureComptableUnit
    */

    @Test
    public void testcheckEcritureComptableUnitariesInvalidFormReturnFunctionnalError() {
        EcritureComptable vEcritureComptable = new EcritureComptable();

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les règles de gestion.");
    }

    @Test
    public void testcheckEcritureComptableUnitRG2ReturnFunctionnalError() {

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
    public void testcheckEcritureComptableUnitRG3ReturnFunctionnalError() {
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
    public void testcheckEcritureComptableUnitRG5BadJournalCodeReturnFunctionnalError() {
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
    public void testcheckEcritureComptableUnitRG5BadYearReturnFunctionnalError() {
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
    public void testcheckEcritureComptableUnitRG5NullValueReturnFunctionnalError() {
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
}

