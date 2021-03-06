package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */

public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable)  {
        // TODO à implémenter
        SequenceEcritureComptable vSequenceEcritureComptable = null;
        // Bien se réferer à la JavaDoc de cette méthode !
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)*/
        try { vSequenceEcritureComptable = getDaoProxy().getComptabiliteDao().getSequenceEcritureComptable(
                pEcritureComptable.getJournal().getCode(),
                pEcritureComptable.getDate().getYear());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        /*  2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1 */

        if(vSequenceEcritureComptable == null) {
            Calendar calendar = Calendar.getInstance();
            vSequenceEcritureComptable = new SequenceEcritureComptable(pEcritureComptable.getJournal(),calendar.get(Calendar.YEAR),1);
        } else {
            vSequenceEcritureComptable.setDerniereValeur(vSequenceEcritureComptable.getDerniereValeur() + 1);
        }

        // 3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)

        String vReferenceFormated = String.format("%05d", vSequenceEcritureComptable.getDerniereValeur());
        pEcritureComptable.setReference(vReferenceFormated);

         /* 4.  Enregistrer (insert/update) la valeur de la séquence en persitance
                    (table sequence_ecriture_comptable) */

        getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);

        if (vSequenceEcritureComptable.getDerniereValeur() == 1) {
            getDaoProxy().getComptabiliteDao()
                    .insertSequenceEcritureComptable(vSequenceEcritureComptable);
        } else {
            getDaoProxy().getComptabiliteDao()
                    .updateSequenceEcritureComptable(vSequenceEcritureComptable);
        }
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                    new ConstraintViolationException(
                            "L'écriture comptable ne respecte pas les contraintes de validation",
                            vViolations));
        }
        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }

        if (pEcritureComptable.getListLigneEcriture().size() < 2
                || vNbrCredit < 1
                || vNbrDebit < 1) {
            throw new FunctionalException(
                    "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence

        /*	La référence d'une écriture comptable est composée du code du journal dans lequel figure l'écriture suivi de
         l'année et d'un numéro de séquence (propre à chaque journal) sur 5 chiffres incrémenté automatiquement à chaque
         écriture. Le formatage de la référence est : XX-AAAA/#####.
          Ex : Journal de banque (BQ), écriture au 31/12/2016 -> BQ-2016/00001 */

        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...

        if(pEcritureComptable.getReference() != null) {

            String vReference = pEcritureComptable.getReference();
            String[] testReference = vReference.split("[-/]");
            Calendar calendar = Calendar.getInstance();
            int annee = calendar.get(Calendar.YEAR);


            if(!testReference[0].equals(pEcritureComptable.getJournal().getCode())) {
                throw new FunctionalException(
                        "Le code journal " + pEcritureComptable.getJournal().getCode() + " ne correspond à celui de la référence "
                                + testReference[0]);
            }

            if(!testReference[1].equals(String.valueOf(annee))) {
                throw new FunctionalException("L'année de référence ne correspond pas à l'année courante");
            }

            try {
                SequenceEcritureComptable vSequenceEcritureComptable = getDaoProxy().getComptabiliteDao()
                        .getSequenceEcritureComptable(
                                pEcritureComptable.getJournal().getCode(),
                                pEcritureComptable.getDate().getYear());

                if(!testReference[2].equals(String.valueOf(String.format("%05d", vSequenceEcritureComptable.getDerniereValeur())))){
                     throw new FunctionalException("Le numéro de séquence de la référence"
                            + vSequenceEcritureComptable.getDerniereValeur() +
                             " ne correspond pas pas à la dernière séquence du journal : "
                             + testReference[2]);
                }
            } catch (NotFoundException e) {
                System.err.println("La séquence d'écriture comptable n'existe pas.");
            }


        }

        if(pEcritureComptable.getReference() == null)
            throw new FunctionalException("La référence de l'écriture comptable ne peut pas être nulle");
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                        pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                        || !pEcritureComptable.getId().equals(vECRef.getId())) {
                   throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
