package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


@SpringJUnitConfig(locations = {
        "classpath:bootstrapContext.xml"})

public class ComptabiliteDaoImplTest extends AbstractDbConsumer {

    @Autowired
    ComptabiliteDao comptabiliteDao;

    /* ListCompteComptable */

    @Test
    void testGetListCompteComptableShouldReturnListNotEmpty() {
        assertThat(comptabiliteDao.getListCompteComptable().isEmpty()).isFalse();
    }

    @Test
    void testGetListJournalComptableShouldReturnListNotEmpty() {
        List<JournalComptable> listJournal = comptabiliteDao.getListJournalComptable();

        assertFalse(listJournal.isEmpty());
    }

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
    void testGetEcritureComptableByRefReturnEcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = comptabiliteDao.getEcritureComptableByRef("AC-2016/00001");

        assertNotNull(ecritureComptable);
    }

    @Test
    void loadListLigneEcriture() {
    }

    @Test
    void insertEcritureComptable() {
    }

    @Test
    void insertListLigneEcritureComptable() {
    }

    @Test
    void updateEcritureComptable() {
    }

    @Test
    void deleteEcritureComptable() {
    }

    @Test
    void deleteListLigneEcritureComptable() {
    }

    @Test
    void getSequenceEcritureComptable() {
    }

    @Test
    void insertSequenceEcritureComptable() {
    }

    @Test
    void updateSequenceEcritureComptable() {
    }
}
