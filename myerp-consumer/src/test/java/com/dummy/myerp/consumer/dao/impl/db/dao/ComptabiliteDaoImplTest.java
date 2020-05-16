package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringJUnitConfig(locations = {
        "classpath:bootstrapContext.xml"})

public class ComptabiliteDaoImplTest extends AbstractDbConsumer {

    @Autowired
    ComptabiliteDao comptabiliteDao;

    /* CompteComptable */

    @Test
    void testGetListCompteComptableShouldReturnListNotEmpty() {
        assertThat(comptabiliteDao.getListCompteComptable().isEmpty()).isFalse();
    }


    /* Journal */

    @Test
    void testGetListJournalComptableShouldReturnListNotEmpty() {
        List<JournalComptable> listJournal = comptabiliteDao.getListJournalComptable();
        assertFalse(listJournal.isEmpty());
    }

    /* Ecriture Comptable */

    @Test
    void testGetListEcritureComptableShouldReturnListNotEmpty() {
        List<EcritureComptable> listEcritureComptable = comptabiliteDao.getListEcritureComptable();
        assertFalse(listEcritureComptable.isEmpty());
    }

    @Test
    void testGetEcritureComptableGivenIDReturnNotNull() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptable(-2);
        assertNotNull(ecritureComptable);
    }

    @Test
    void testGetEcritureComptableGivenWrongIDReturnException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> comptabiliteDao.getEcritureComptable(2));
    }

    @Test
    void testGetEcritureComptableByRefGivenRefReturnEcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptableByRef("AC-2016/00001");
        assertNotNull(ecritureComptable);
    }

    @Test
    void testGetEcritureComptableByRefGivenWrongRefReturnException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> comptabiliteDao.getEcritureComptableByRef("AC-1016/00001"));
    }

    @Test
    void testInsertEcritureComptableGivenValidInsertReturnNotNull() {
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
    void testInsertEcritureComptableGivenInvalidInsertReturnException() {
        EcritureComptable ecritureComptable = new EcritureComptable();
        JournalComptable journal = new JournalComptable();
        ecritureComptable.setJournal(journal);

        assertThrows(Exception.class, () -> comptabiliteDao.insertEcritureComptable(ecritureComptable));
    }

//    @Test
//    void updateEcritureComptable() {
//    }
//
//    @Test
//    void deleteEcritureComptable() {
//    }
//
//    /* LigneEcritureComptable */
//
//    @Test
//    void insertListLigneEcritureComptable() {
//
//    }
//
//    @Test
//    void deleteListLigneEcritureComptable() {
//    }

    /* SequenceEcritureComptable */

    @Test
    void testGetSequenceEcritureComptableGivenValidValueReturnSequenceEcritureComptable() throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDao.getSequenceEcritureComptable("AC",2016);
        assertThat(sequenceEcritureComptable).isNotNull();
    }

    @Test
    void testGetSequenceEcritureComptableGivenInvalidValueReturnNotFound() throws NotFoundException {
        assertThrows(NotFoundException.class,() -> comptabiliteDao.getSequenceEcritureComptable("TEST",2020));
    }

//    @Test
//    void testInsertSequenceEcritureComptableGivenValidValueReturnSequenceEcritureComptable() {
//
//    }
//
//    @Test
//    void testInsertSequenceEcritureComptableGivenInvalidValueReturnException() throws NotFoundException {
//        //Given
//        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDao.getSequenceEcritureComptable("AC", 2016);
//
//        //When
//        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable);
//
//        //Then
//        assertThrows(FunctionalException.class, () -> comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable));
//    }
//
//    @Test
//    void updateSequenceEcritureComptable() {
//    }
}
