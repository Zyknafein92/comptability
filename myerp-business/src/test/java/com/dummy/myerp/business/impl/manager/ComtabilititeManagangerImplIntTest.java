package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
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

    @Mock
    ComptabiliteManagerImpl manager;

    @Mock
    TransactionStatus vTS;

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
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(453), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(453)));
    }

   /*
                                 AddReference
    */

    @Test
    public void testAddReference_GivenNew_ReturnNew() throws Exception {
        when(comptabiliteDao.getSequenceEcritureComptable(vEcritureComptable.getJournal().getCode(), vEcritureComptable.getDate().getYear()))
                .thenReturn(null);

        manager.addReference(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable(any());
    }

    @Test
    public void testAddReference_GivenExistingValue_ReturnUpdateValue() throws Exception {
        JournalComptable pJournal = new JournalComptable("AC", "Achat");
        SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable(pJournal,2020, 00001);

        when(comptabiliteDao.getSequenceEcritureComptable(vEcritureComptable.getJournal().getCode(), vEcritureComptable.getDate().getYear()))
                .thenReturn(vSequenceEcritureComptable);

        manager.addReference(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(comptabiliteDao, times(1)).updateSequenceEcritureComptable(any());
    }

    /*
                             checkEcritureComptable
     */

    @Test
    public void testCheckEcritureComptable_GivenValidValue_ReturnValid() throws FunctionalException, NotFoundException {
        vEcritureComptable.setId(1);
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(
                vEcritureComptable.getJournal(),2020,1);

        when(comptabiliteDao.getEcritureComptableByRef("AC-2020/00001")).thenReturn(vEcritureComptable);
        when(comptabiliteDao.getSequenceEcritureComptable(anyString(),anyInt())).thenReturn(sequenceEcritureComptable);

        manager.checkEcritureComptable(vEcritureComptable);
    }


    /*
                             checkEcritureComptableUnit
     */

    @Test
    public void testCheckEcritureComptableUnitRG5_GivenBadSequenceValue_ReturnFunctionnalError() throws NotFoundException {
        vEcritureComptable.setReference("AC-2020/00005");

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(
                vEcritureComptable.getJournal(),2020,3);

        given(comptabiliteDao.getSequenceEcritureComptable(vEcritureComptable.getJournal().getCode(), vEcritureComptable.getDate().getYear()))
                .willReturn(sequenceEcritureComptable);

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo(("Le numéro de séquence de la référence"
                        + sequenceEcritureComptable.getDerniereValeur() +
                        " ne correspond pas pas à la dernière séquence du journal : "
                        + "00005"));
    }

    /*
                                 checkEcritureComptableContext
     */

    @Test
    public void testCheckEcritureComptableContextRG6_GivenNewRef_ReturnFunctionalError() throws NotFoundException {
        // id == null de base
        given(comptabiliteDao.getEcritureComptableByRef(anyString())).willReturn(vEcritureComptable);

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptableContext(vEcritureComptable));

        assertThat("Une autre écriture comptable existe déjà avec la même référence.")
                .isEqualTo(functionalException.getMessage());
    }

    @Test
    public void testCheckEcritureComptableContextRG6_GivenExistingRef_ReturnFunctionalError() throws NotFoundException, FunctionalException {
        vEcritureComptable.setId(1);

        EcritureComptable testEcritureComptable = new EcritureComptable();
        testEcritureComptable.setId(2);

        when(comptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(testEcritureComptable);

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> manager.checkEcritureComptableContext(vEcritureComptable));

        assertThat("Une autre écriture comptable existe déjà avec la même référence.")
                .isEqualTo(functionalException.getMessage());
    }

    /*
                                    InsertEcritureComptable
     */

    @Test
    public void testInsertEcritureComptableShouldCallTransactionManager() throws FunctionalException, NotFoundException {
        vTS = transactionManager.beginTransactionMyERP();
        vEcritureComptable.setId(1);
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(
                vEcritureComptable.getJournal(),2020,1);

        when(comptabiliteDao.getEcritureComptableByRef("AC-2020/00001")).thenReturn(vEcritureComptable);
        when(comptabiliteDao.getSequenceEcritureComptable(anyString(),anyInt())).thenReturn(sequenceEcritureComptable);

        manager.insertEcritureComptable(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(transactionManager, times(1)).commitMyERP(vTS);
        verify(transactionManager, times(1)).rollbackMyERP(vTS);
    }

    /*
                                     UpdateEcritureComptable
     */

    @Test
    public void testUpdateEcritureComptableShouldCallTransactionManager() throws NotFoundException, FunctionalException {
        vTS = transactionManager.beginTransactionMyERP();
        vEcritureComptable.setId(1);
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(
                vEcritureComptable.getJournal(),2020,1);

        when(comptabiliteDao.getEcritureComptableByRef("AC-2020/00001")).thenReturn(vEcritureComptable);
        when(comptabiliteDao.getSequenceEcritureComptable(anyString(),anyInt())).thenReturn(sequenceEcritureComptable);

        manager.updateEcritureComptable(vEcritureComptable);

        verify(dao, times(3)).getComptabiliteDao();
        verify(transactionManager, times(1)).commitMyERP(vTS);
        verify(transactionManager, times(1)).rollbackMyERP(vTS);
    }
    
    /*
                                     DeleteEcritureComptable
     */
    @Test
    public void testDeleteEcritureComptableShouldCallTransactionManager() {
        vTS = transactionManager.beginTransactionMyERP();
        vEcritureComptable.setId(1);

        manager.deleteEcritureComptable(vEcritureComptable.getId());
        verify(dao, times(1)).getComptabiliteDao();
        verify(transactionManager, times(1)).commitMyERP(vTS);
        verify(transactionManager, times(1)).rollbackMyERP(vTS);
    }
}

