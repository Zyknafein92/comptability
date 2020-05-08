package com.dummy.myerp.model.bean.comptabilite;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Bean représentant une séquence pour les références d'écriture comptable
 */
public class SequenceEcritureComptable {

    // ==================== Attributs ====================


    /**
     * Code du journal comptable
     */
    @NotNull
    @Size(max = 5)
    private JournalComptable journalCode;

    /** L'année */
    private Integer annee;
    /** La dernière valeur utilisée */
    private Integer derniereValeur;

    // ==================== Constructeurs ====================
    /**
     * Constructeur
     */
    public SequenceEcritureComptable() {
    }

    /**
     * Constructeur
     *
     * @param pAnnee -
     * @param pDerniereValeur -
     */
    public SequenceEcritureComptable(Integer pAnnee, Integer pDerniereValeur) {
        annee = pAnnee;
        derniereValeur = pDerniereValeur;
    }

    public SequenceEcritureComptable(JournalComptable pJournalCode, Integer pAnnee, Integer pDerniereValeur) {
        journalCode = pJournalCode;
        annee = pAnnee;
        derniereValeur = pDerniereValeur;
    }

    // ==================== Getters/Setters ====================
    public Integer getAnnee() {
        return annee;
    }
    public void setAnnee(Integer pAnnee) {
        annee = pAnnee;
    }
    public Integer getDerniereValeur() {
        return derniereValeur;
    }
    public void setDerniereValeur(Integer pDerniereValeur) {
        derniereValeur = pDerniereValeur;
    }
    public JournalComptable getJournalCode() { return journalCode; }
    public void setJournalCode(JournalComptable pJournalCode) {journalCode = pJournalCode;}

    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
                .append("annee=").append(annee)
                .append(vSEP).append("derniereValeur=").append(derniereValeur)
                .append("}");
        return vStB.toString();
    }
}
