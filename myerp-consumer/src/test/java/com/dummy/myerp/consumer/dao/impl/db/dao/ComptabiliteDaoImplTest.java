package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringJUnitConfig(locations = {"classpath:bootstrapContext.xml"})

public class ComptabiliteDaoImplTest extends AbstractDbConsumer {

    @Autowired
    ComptabiliteDao comptabiliteDao;

    /* CompteComptable */

    @Test
    void testGetListCompteComptable_ShouldReturnListNotEmpty() {
        assertThat(comptabiliteDao.getListCompteComptable().isEmpty()).isFalse();
    }

    /* Journal */

    @Test
    void testGetListJournalComptable_ShouldReturnListNotEmpty() {
        List<JournalComptable> listJournal = comptabiliteDao.getListJournalComptable();
        assertFalse(listJournal.isEmpty());
    }

    /* Ecriture Comptable */

    @Test
    void testGetListEcritureComptable_ShouldReturnListNotEmpty() {
        List<EcritureComptable> listEcritureComptable = comptabiliteDao.getListEcritureComptable();
        assertFalse(listEcritureComptable.isEmpty());
    }

    @Test
    void testGetEcritureComptable_GivenID_ReturnNotNull() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptable(-2);
        assertNotNull(ecritureComptable);
    }

    @Test
    void testGetEcritureComptable_GivenWrongID_ReturnException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> comptabiliteDao.getEcritureComptable(2));
    }

    @Test
    void testGetEcritureComptableByRef_GivenRef_ReturnEcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptableByRef("AC-2016/00001");
        assertNotNull(ecritureComptable);
    }

    @Test
    void testGetEcritureComptableByRef_GivenWrongRef_ReturnException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> comptabiliteDao.getEcritureComptableByRef("AC-1016/00001"));
    }

    @Test
    void testInsertEcritureComptable_GivenValidInsert_ReturnNotNull() {
        //Given
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("test");
        ecritureComptable.setDate(new Date());

        JournalComptable journal = new JournalComptable();
        journal.setCode("AC");
        journal.setLibelle("Test");

        ecritureComptable.setJournal(journal);
        ecritureComptable.setReference("AC-2016/00002");

        //When
        comptabiliteDao.insertEcritureComptable(ecritureComptable);

        //Then
        assertNotNull(ecritureComptable.getId());

        //CleanBDD
        comptabiliteDao.deleteEcritureComptable(ecritureComptable.getId());
    }

    @Test
    void testInsertEcritureComptable_GivenInvalidInsert_ReturnException() {
        EcritureComptable ecritureComptable = new EcritureComptable();
        JournalComptable journal = new JournalComptable();
        ecritureComptable.setJournal(journal);

        assertThrows(Exception.class, () -> comptabiliteDao.insertEcritureComptable(ecritureComptable));
    }

    @Test
    void testUpdateEcritureComptable_GivenNewValue_ReturnEcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptable(-1);
        ecritureComptable.setLibelle("Test");

        comptabiliteDao.updateEcritureComptable(ecritureComptable);

        EcritureComptable ecritureComptableTest = comptabiliteDao.getEcritureComptable(-1);

        assertThat(ecritureComptableTest.getLibelle()).isEqualTo("Test");
    }

    @Test
    void testDeleteEcritureComptable_GivenID_ReturnListLessOne() {
       int listEcritureComptableSize = comptabiliteDao.getListEcritureComptable().size();

       comptabiliteDao.deleteEcritureComptable(-1);

       assertThat(listEcritureComptableSize - 1).isEqualTo(comptabiliteDao.getListEcritureComptable().size());
    }

    /* SequenceEcritureComptable */

    @Test
    void testGetSequenceEcritureComptable_GivenValidValue_ReturnSequenceEcritureComptable() throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDao.getSequenceEcritureComptable("AC",2016);
        assertThat(sequenceEcritureComptable).isNotNull();
    }

    @Test
    void testGetSequenceEcritureComptable_GivenInvalidValue_ReturnNotFound() throws NotFoundException {
        assertThrows(NotFoundException.class,() -> comptabiliteDao.getSequenceEcritureComptable("TEST",2020));
    }
}
